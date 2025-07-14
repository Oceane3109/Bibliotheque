package com.bibliotheque.controller;

import com.bibliotheque.model.Reservation;
import com.bibliotheque.model.Adherent;
import com.bibliotheque.model.Livre;
import com.bibliotheque.model.PretLivre;
import com.bibliotheque.model.Exemplaire;
import com.bibliotheque.model.TypePret;
import com.bibliotheque.service.ReservationService;
import com.bibliotheque.service.AdherentService;
import com.bibliotheque.service.LivreService;
import com.bibliotheque.service.PretLivreService;
import com.bibliotheque.service.ExemplaireService;
import com.bibliotheque.service.TypePretService;
import com.bibliotheque.model.Notification;
import com.bibliotheque.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/reservations")
@PreAuthorize("hasRole('ADMIN')")
public class ReservationController {
    private final ReservationService reservationService;
    private final AdherentService adherentService;
    private final LivreService livreService;
    private final PretLivreService pretLivreService;
    private final ExemplaireService exemplaireService;
    private final TypePretService typePretService;
    private final NotificationService notificationService;

    @Autowired
    public ReservationController(ReservationService reservationService, AdherentService adherentService, LivreService livreService, PretLivreService pretLivreService, ExemplaireService exemplaireService, TypePretService typePretService, NotificationService notificationService) {
        this.reservationService = reservationService;
        this.adherentService = adherentService;
        this.livreService = livreService;
        this.pretLivreService = pretLivreService;
        this.exemplaireService = exemplaireService;
        this.typePretService = typePretService;
        this.notificationService = notificationService;
    }

    @GetMapping
    public String listReservations(Model model) {
        List<Reservation> reservations = reservationService.getAllReservations();
        model.addAttribute("reservations", reservations);
        return "admin/reservations/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("reservation", new Reservation());
        model.addAttribute("adherents", adherentService.getAllAdherents());
        model.addAttribute("livres", livreService.getAllLivres());
        return "admin/reservations/form";
    }

    @PostMapping("/create")
    public String createReservation(@Valid @ModelAttribute("reservation") Reservation reservation,
                                    BindingResult result,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("adherents", adherentService.getAllAdherents());
            model.addAttribute("livres", livreService.getAllLivres());
            return "admin/reservations/form";
        }
        reservationService.saveReservation(reservation);
        redirectAttributes.addFlashAttribute("success", "Réservation enregistrée avec succès");
        return "redirect:/admin/reservations";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Reservation> reservationOpt = reservationService.getReservationById(id);
        if (reservationOpt.isEmpty()) {
            return "redirect:/admin/reservations";
        }
        model.addAttribute("reservation", reservationOpt.get());
        model.addAttribute("adherents", adherentService.getAllAdherents());
        model.addAttribute("livres", livreService.getAllLivres());
        return "admin/reservations/form";
    }

    @PostMapping("/edit/{id}")
    public String updateReservation(@PathVariable Long id,
                                    @Valid @ModelAttribute("reservation") Reservation reservation,
                                    BindingResult result,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("adherents", adherentService.getAllAdherents());
            model.addAttribute("livres", livreService.getAllLivres());
            return "admin/reservations/form";
        }
        reservation.setIdReservation(id);
        reservationService.saveReservation(reservation);
        redirectAttributes.addFlashAttribute("success", "Réservation modifiée avec succès");
        return "redirect:/admin/reservations";
    }

    @PostMapping("/delete/{id}")
    public String deleteReservation(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        reservationService.deleteReservation(id);
        redirectAttributes.addFlashAttribute("success", "Réservation supprimée avec succès");
        return "redirect:/admin/reservations";
    }

    @PostMapping("/confirm/{id}")
    public String confirmerReservation(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<Reservation> reservationOpt = reservationService.getReservationById(id);
        if (reservationOpt.isPresent()) {
            Reservation reservation = reservationOpt.get();
            if (reservation.getDatePret() == null || reservation.getDateFinPret() == null || reservation.getTypePret() == null) {
                redirectAttributes.addFlashAttribute("error", "Impossible de confirmer la réservation : date de prêt, date de fin ou type de prêt manquant.");
                return "redirect:/admin/reservations";
            }
            reservation.setEtatReservation("confirmee");
            reservationService.saveReservation(reservation);
            // Notifier l'adhérent
            Notification notif = new Notification();
            notif.setAdherent(reservation.getAdherent());
            notif.setTitre("Demande de réservation acceptée");
            notif.setMessage("Votre demande de réservation pour le livre '" + reservation.getLivre().getTitre() + "' a été acceptée. Un prêt a été créé.");
            notif.setDateEnvoi(java.time.LocalDateTime.now());
            notif.setLu(false);
            notificationService.saveNotification(notif);
            // Création automatique du prêt
            List<Exemplaire> exemplairesDispos = exemplaireService.getExemplairesDisponiblesByLivre(reservation.getLivre());
            if (!exemplairesDispos.isEmpty()) {
                Exemplaire exemplaire = exemplairesDispos.get(0);
                TypePret typePret = reservation.getTypePret();
                PretLivre pret = new PretLivre();
                pret.setAdherent(reservation.getAdherent());
                pret.setExemplaire(exemplaire);
                pret.setTypePret(typePret);
                java.time.LocalDate datePret = reservation.getDatePret() != null ? reservation.getDatePret() : java.time.LocalDate.now();
                pret.setDateDebut(datePret);
                pret.setDatePret(datePret);
                if (reservation.getDateFinPret() != null) {
                    pret.setDateFin(reservation.getDateFinPret());
                } else {
                    pret.setDateFin(java.time.LocalDate.now().plusDays(30)); // Défaut à 30 jours
                }
                pret.setEtatPret("en_cours");
                pretLivreService.savePret(pret);
                exemplaire.setEtat("en_pret");
                exemplaireService.saveExemplaire(exemplaire);
                redirectAttributes.addFlashAttribute("success", "Réservation confirmée et prêt créé avec succès");
            } else {
                redirectAttributes.addFlashAttribute("error", "Aucun exemplaire disponible pour ce livre.");
            }
        }
        return "redirect:/admin/reservations";
    }
}
