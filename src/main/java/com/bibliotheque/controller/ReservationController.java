package com.bibliotheque.controller;

import com.bibliotheque.model.Reservation;
import com.bibliotheque.model.Adherent;
import com.bibliotheque.model.Livre;
import com.bibliotheque.service.ReservationService;
import com.bibliotheque.service.AdherentService;
import com.bibliotheque.service.LivreService;
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

    @Autowired
    public ReservationController(ReservationService reservationService, AdherentService adherentService, LivreService livreService) {
        this.reservationService = reservationService;
        this.adherentService = adherentService;
        this.livreService = livreService;
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
}
