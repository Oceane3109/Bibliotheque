package com.bibliotheque.controller;

import com.bibliotheque.model.RetourLivre;
import com.bibliotheque.model.PretLivre;
import com.bibliotheque.service.RetourLivreService;
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
@RequestMapping("/admin/retours")
@PreAuthorize("hasRole('ADMIN')")
public class RetourLivreController {
    private final RetourLivreService retourLivreService;
    private final PretLivreService pretLivreService;

    @Autowired
    public RetourLivreController(RetourLivreService retourLivreService, PretLivreService pretLivreService) {
        this.retourLivreService = retourLivreService;
        this.pretLivreService = pretLivreService;
    }

    @GetMapping
    public String listRetours(Model model) {
        List<RetourLivre> retours = retourLivreService.getAllRetours();
        model.addAttribute("retours", retours);
        return "admin/retours/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("retourLivre", new RetourLivre());
        model.addAttribute("prets", pretLivreService.getAllPrets());
        return "admin/retours/form";
    }

    @PostMapping("/create")
    public String createRetour(@Valid @ModelAttribute("retourLivre") RetourLivre retourLivre,
                               BindingResult result,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("prets", pretLivreService.getAllPrets());
            return "admin/retours/form";
        }
        retourLivreService.saveRetour(retourLivre);
        redirectAttributes.addFlashAttribute("success", "Retour enregistré avec succès");
        return "redirect:/admin/retours";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<RetourLivre> retourOpt = retourLivreService.getRetourById(id);
        if (retourOpt.isEmpty()) {
            return "redirect:/admin/retours";
        }
        model.addAttribute("retourLivre", retourOpt.get());
        model.addAttribute("prets", pretLivreService.getAllPrets());
        return "admin/retours/form";
    }

    @PostMapping("/edit/{id}")
    public String updateRetour(@PathVariable Long id,
                               @Valid @ModelAttribute("retourLivre") RetourLivre retourLivre,
                               BindingResult result,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("prets", pretLivreService.getAllPrets());
            return "admin/retours/form";
        }
        retourLivre.setIdRetour(id);
        retourLivreService.saveRetour(retourLivre);
        redirectAttributes.addFlashAttribute("success", "Retour modifié avec succès");
        return "redirect:/admin/retours";
    }

    @PostMapping("/delete/{id}")
    public String deleteRetour(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        retourLivreService.deleteRetour(id);
        redirectAttributes.addFlashAttribute("success", "Retour supprimé avec succès");
        return "redirect:/admin/retours";
    }

    @GetMapping("/create/{pretId}")
    public String showCreateFormFromPret(@PathVariable Long pretId, Model model) {
        var pretOpt = pretLivreService.getPretById(pretId);
        if (pretOpt.isEmpty()) {
            return "redirect:/admin/prets/list";
        }
        RetourLivre retour = new RetourLivre();
        retour.setPretLivre(pretOpt.get());
        retour.setDateRetour(java.time.LocalDate.now());
        model.addAttribute("retourLivre", retour);
        model.addAttribute("prets", pretLivreService.getAllPrets());
        return "admin/retours/form";
    }
} 