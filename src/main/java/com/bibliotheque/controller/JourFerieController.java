package com.bibliotheque.controller;

import com.bibliotheque.model.JourFerie;
import com.bibliotheque.service.JourFerieService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/admin/jours-feries")
@PreAuthorize("hasRole('ADMIN')")
public class JourFerieController {
    private final JourFerieService jourFerieService;

    @Autowired
    public JourFerieController(JourFerieService jourFerieService) {
        this.jourFerieService = jourFerieService;
    }

    @GetMapping
    public String listJoursFeries(Model model) {
        model.addAttribute("joursFeries", jourFerieService.getAllJoursFeries());
        return "admin/jours-feries/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("jourFerie", new JourFerie());
        return "admin/jours-feries/form";
    }

    @PostMapping("/create")
    public String createJourFerie(@Valid @ModelAttribute("jourFerie") JourFerie jourFerie,
                                  BindingResult result,
                                  RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/jours-feries/form";
        }
        if (jourFerieService.isJourFerie(jourFerie.getDateFeriee())) {
            result.rejectValue("dateFeriee", "duplicate", "Cette date est déjà un jour férié");
            return "admin/jours-feries/form";
        }
        jourFerieService.saveJourFerie(jourFerie);
        redirectAttributes.addFlashAttribute("success", "Jour férié ajouté avec succès");
        return "redirect:/admin/jours-feries";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<JourFerie> jourFerieOpt = jourFerieService.getJourFerieById(id);
        if (jourFerieOpt.isEmpty()) {
            return "redirect:/admin/jours-feries";
        }
        model.addAttribute("jourFerie", jourFerieOpt.get());
        return "admin/jours-feries/form";
    }

    @PostMapping("/edit/{id}")
    public String updateJourFerie(@PathVariable Long id,
                                  @Valid @ModelAttribute("jourFerie") JourFerie jourFerie,
                                  BindingResult result,
                                  RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/jours-feries/form";
        }
        jourFerie.setIdJourFerie(id);
        jourFerieService.saveJourFerie(jourFerie);
        redirectAttributes.addFlashAttribute("success", "Jour férié modifié avec succès");
        return "redirect:/admin/jours-feries";
    }

    @PostMapping("/delete/{id}")
    public String deleteJourFerie(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        jourFerieService.deleteJourFerie(id);
        redirectAttributes.addFlashAttribute("success", "Jour férié supprimé avec succès");
        return "redirect:/admin/jours-feries";
    }
} 