package com.bibliotheque.controller;

import com.bibliotheque.model.Penalite;
import com.bibliotheque.model.PretLivre;
import com.bibliotheque.model.Adherent;
import com.bibliotheque.service.PenaliteService;
import com.bibliotheque.service.PretLivreService;
import com.bibliotheque.service.AdherentService;
import com.bibliotheque.service.JourFerieService;
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
@RequestMapping("/admin/penalites")
@PreAuthorize("hasRole('ADMIN')")
public class PenaliteController {
    private final PenaliteService penaliteService;
    private final PretLivreService pretLivreService;
    private final AdherentService adherentService;
    private final JourFerieService jourFerieService;

    @Autowired
    public PenaliteController(PenaliteService penaliteService, PretLivreService pretLivreService, AdherentService adherentService, JourFerieService jourFerieService) {
        this.penaliteService = penaliteService;
        this.pretLivreService = pretLivreService;
        this.adherentService = adherentService;
        this.jourFerieService = jourFerieService;
    }

    @GetMapping
    public String listPenalites(Model model) {
        List<Penalite> penalites = penaliteService.getAllPenalites();
        model.addAttribute("penalites", penalites);
        return "admin/penalites/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("penalite", new Penalite());
        model.addAttribute("prets", pretLivreService.getAllPrets());
        return "admin/penalites/form";
    }

    @PostMapping("/create")
    public String createPenalite(@Valid @ModelAttribute("penalite") Penalite penalite,
                                 BindingResult result,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("prets", pretLivreService.getAllPrets());
            return "admin/penalites/form";
        }
        // Calcul automatique des jours de pénalité pour motif retard (inclut samedi)
        if ("retard".equals(penalite.getMotif()) && penalite.getPretLivre() != null && penalite.getDateEmission() != null) {
            var pret = penalite.getPretLivre();
            var dateRetour = penalite.getDateEmission();
            var dateFin = pret.getDateFin();
            if (dateRetour.isAfter(dateFin)) {
                int joursRetard = 0;
                var d = dateFin.plusDays(1);
                while (!d.isAfter(dateRetour)) {
                    if (!jourFerieService.isJourFerie(d) && d.getDayOfWeek().getValue() != 7) { // Exclut dimanche et jours fériés
                        joursRetard++;
                    }
                    d = d.plusDays(1);
                }
                penalite.setJoursPenalite(joursRetard);
            } else {
                penalite.setJoursPenalite(0);
            }
        }
        penaliteService.savePenalite(penalite);
        redirectAttributes.addFlashAttribute("success", "Pénalité enregistrée avec succès");
        return "redirect:/admin/penalites";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Penalite> penaliteOpt = penaliteService.getPenaliteById(id);
        if (penaliteOpt.isEmpty()) {
            return "redirect:/admin/penalites";
        }
        model.addAttribute("penalite", penaliteOpt.get());
        model.addAttribute("prets", pretLivreService.getAllPrets());
        return "admin/penalites/form";
    }

    @PostMapping("/edit/{id}")
    public String updatePenalite(@PathVariable Long id,
                                 @Valid @ModelAttribute("penalite") Penalite penalite,
                                 BindingResult result,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("prets", pretLivreService.getAllPrets());
            return "admin/penalites/form";
        }
        penalite.setIdPenalite(id);
        // Calcul automatique des jours de pénalité pour motif retard (inclut samedi)
        if ("retard".equals(penalite.getMotif()) && penalite.getPretLivre() != null && penalite.getDateEmission() != null) {
            var pret = penalite.getPretLivre();
            var dateRetour = penalite.getDateEmission();
            var dateFin = pret.getDateFin();
            if (dateRetour.isAfter(dateFin)) {
                int joursRetard = 0;
                var d = dateFin.plusDays(1);
                while (!d.isAfter(dateRetour)) {
                    if (!jourFerieService.isJourFerie(d) && d.getDayOfWeek().getValue() != 7) { // Exclut dimanche et jours fériés
                        joursRetard++;
                    }
                    d = d.plusDays(1);
                }
                penalite.setJoursPenalite(joursRetard);
            } else {
                penalite.setJoursPenalite(0);
            }
        }
        penaliteService.savePenalite(penalite);
        redirectAttributes.addFlashAttribute("success", "Pénalité modifiée avec succès");
        return "redirect:/admin/penalites";
    }

    @PostMapping("/delete/{id}")
    public String deletePenalite(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        penaliteService.deletePenalite(id);
        redirectAttributes.addFlashAttribute("success", "Pénalité supprimée avec succès");
        return "redirect:/admin/penalites";
    }

    @GetMapping("/list")
    public String listPenalitesList(Model model) {
        return listPenalites(model);
    }
} 