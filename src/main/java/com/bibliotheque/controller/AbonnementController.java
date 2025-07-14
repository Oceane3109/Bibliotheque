package com.bibliotheque.controller;

import com.bibliotheque.model.Abonnement;
import com.bibliotheque.model.Adherent;
import com.bibliotheque.service.AbonnementService;
import com.bibliotheque.service.AdherentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/abonnement")
public class AbonnementController {
    @Autowired
    private AbonnementService abonnementService;
    @Autowired
    private AdherentService adherentService;

    // Afficher le statut de l’abonnement pour l’adhérent
    @GetMapping("/statut")
    public String statutAbonnement(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Adherent adherent = adherentService.getAdherentByUserUsername(userDetails.getUsername()).orElse(null);
        Abonnement abonnement = null;
        if (adherent != null) {
            abonnement = abonnementService.getAbonnementActifByAdherent(adherent).orElse(null);
        }
        model.addAttribute("abonnement", abonnement);
        return "adherent/abonnement-statut";
    }

    // Demander un abonnement (formulaire ou bouton)
    @PostMapping("/demander")
    public String demanderAbonnement(@AuthenticationPrincipal UserDetails userDetails) {
        Adherent adherent = adherentService.getAdherentByUserUsername(userDetails.getUsername()).orElse(null);
        if (adherent != null) {
            // Créer une demande d'abonnement en attente
            Abonnement abonnement = new Abonnement();
            abonnement.setAdherent(adherent);
            abonnement.setDateDebut(java.time.LocalDate.now());
            abonnement.setDateFin(java.time.LocalDate.now().plusMonths(12)); // 1 an par défaut
            abonnement.setStatut("en_attente");
            abonnementService.saveAbonnement(abonnement);
        }
        return "redirect:/adherent/profile?abonnementDemande";
    }

    // Renouveler un abonnement (mensuel)
    @PostMapping("/renouveler")
    public String renouvelerAbonnement(@AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        Adherent adherent = adherentService.getAdherentByUserUsername(userDetails.getUsername()).orElse(null);
        if (adherent != null) {
            // Vérifier qu'il n'y a pas déjà un abonnement actif ou en attente
            boolean dejaAbonne = abonnementService.getAbonnementActifByAdherent(adherent).isPresent() ||
                abonnementService.getAbonnementsByAdherent(adherent).stream().anyMatch(a -> "en_attente".equals(a.getStatut()));
            if (dejaAbonne) {
                redirectAttributes.addFlashAttribute("error", "Vous avez déjà un abonnement actif ou en attente.");
                return "redirect:/adherent/profile";
            }
            // Créer une nouvelle demande d'abonnement mensuel
            Abonnement abonnement = new Abonnement();
            abonnement.setAdherent(adherent);
            abonnement.setDateDebut(java.time.LocalDate.now());
            abonnement.setDateFin(java.time.LocalDate.now().plusMonths(1)); // 1 mois
            abonnement.setStatut("en_attente");
            abonnementService.saveAbonnement(abonnement);
            redirectAttributes.addFlashAttribute("success", "Demande de renouvellement envoyée !");
        }
        return "redirect:/adherent/profile";
    }

    // Liste des demandes d’abonnement pour l’admin
    @GetMapping("/admin/demandes")
    public String listeDemandes(Model model) {
        List<Abonnement> demandes = abonnementService.getAbonnementsEnAttente();
        model.addAttribute("demandes", demandes);
        return "admin/abonnement/demandes";
    }

    // Valider une demande d’abonnement
    @PostMapping("/admin/valider/{id}")
    public String validerAbonnement(@PathVariable Long id, @RequestParam(value = "adherentId", required = false) Long adherentId) {
        abonnementService.validerAbonnement(id);
        if (adherentId != null) {
            return "redirect:/admin/adherents/edit/" + adherentId + "?abonnementValide";
        }
        return "redirect:/abonnement/admin/demandes?valide";
    }

    // Refuser une demande d’abonnement
    @PostMapping("/admin/refuser/{id}")
    public String refuserAbonnement(@PathVariable Long id, @RequestParam(value = "adherentId", required = false) Long adherentId) {
        abonnementService.refuserAbonnement(id);
        if (adherentId != null) {
            return "redirect:/admin/adherents/edit/" + adherentId + "?abonnementRefuse";
        }
        return "redirect:/abonnement/admin/demandes?refuse";
    }
} 