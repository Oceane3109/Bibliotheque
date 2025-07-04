package com.bibliotheque.controller;

import com.bibliotheque.model.ProlongementPret;
import com.bibliotheque.model.PretLivre;
import com.bibliotheque.service.ProlongementPretService;
import com.bibliotheque.service.PretLivreService;
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
@RequestMapping("/admin/prolongements")
@PreAuthorize("hasRole('ADMIN')")
public class ProlongementPretController {
    private final ProlongementPretService prolongementPretService;
    private final PretLivreService pretLivreService;

    @Autowired
    public ProlongementPretController(ProlongementPretService prolongementPretService, PretLivreService pretLivreService) {
        this.prolongementPretService = prolongementPretService;
        this.pretLivreService = pretLivreService;
    }

    @GetMapping
    public String listProlongements(Model model) {
        List<ProlongementPret> prolongements = prolongementPretService.getAllProlongements();
        model.addAttribute("prolongements", prolongements);
        return "admin/prolongements/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("prolongementPret", new ProlongementPret());
        model.addAttribute("prets", pretLivreService.getAllPrets());
        return "admin/prolongements/form";
    }

    @PostMapping("/create")
    public String createProlongement(@Valid @ModelAttribute("prolongementPret") ProlongementPret prolongementPret,
                                     BindingResult result,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("prets", pretLivreService.getAllPrets());
            return "admin/prolongements/form";
        }
        prolongementPretService.saveProlongement(prolongementPret);
        redirectAttributes.addFlashAttribute("success", "Prolongement enregistré avec succès");
        return "redirect:/admin/prolongements";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<ProlongementPret> prolongementOpt = prolongementPretService.getProlongementById(id);
        if (prolongementOpt.isEmpty()) {
            return "redirect:/admin/prolongements";
        }
        model.addAttribute("prolongementPret", prolongementOpt.get());
        model.addAttribute("prets", pretLivreService.getAllPrets());
        return "admin/prolongements/form";
    }

    @PostMapping("/edit/{id}")
    public String updateProlongement(@PathVariable Long id,
                                     @Valid @ModelAttribute("prolongementPret") ProlongementPret prolongementPret,
                                     BindingResult result,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("prets", pretLivreService.getAllPrets());
            return "admin/prolongements/form";
        }
        prolongementPret.setIdProlongation(id);
        prolongementPretService.saveProlongement(prolongementPret);
        redirectAttributes.addFlashAttribute("success", "Prolongement modifié avec succès");
        return "redirect:/admin/prolongements";
    }

    @PostMapping("/delete/{id}")
    public String deleteProlongement(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        prolongementPretService.deleteProlongement(id);
        redirectAttributes.addFlashAttribute("success", "Prolongement supprimé avec succès");
        return "redirect:/admin/prolongements";
    }
} 