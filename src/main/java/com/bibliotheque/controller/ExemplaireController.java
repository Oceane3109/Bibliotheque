package com.bibliotheque.controller;

import com.bibliotheque.model.Exemplaire;
import com.bibliotheque.model.Livre;
import com.bibliotheque.service.ExemplaireService;
import com.bibliotheque.service.LivreService;
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
@RequestMapping("/admin/exemplaires")
@PreAuthorize("hasRole('ADMIN')")
public class ExemplaireController {
    private final ExemplaireService exemplaireService;
    private final LivreService livreService;

    @Autowired
    public ExemplaireController(ExemplaireService exemplaireService, LivreService livreService) {
        this.exemplaireService = exemplaireService;
        this.livreService = livreService;
    }

    @GetMapping("/livre/{livreId}")
    public String listExemplaires(@PathVariable Long livreId, Model model) {
        Optional<Livre> livreOpt = livreService.getLivreById(livreId);
        if (livreOpt.isEmpty()) {
            return "redirect:/livres";
        }
        model.addAttribute("livre", livreOpt.get());
        model.addAttribute("exemplaires", exemplaireService.getExemplairesByLivre(livreOpt.get()));
        return "admin/exemplaires/list";
    }

    @GetMapping("/livre/{livreId}/create")
    public String showCreateForm(@PathVariable Long livreId, Model model) {
        Optional<Livre> livreOpt = livreService.getLivreById(livreId);
        if (livreOpt.isEmpty()) {
            return "redirect:/livres";
        }
        Exemplaire exemplaire = new Exemplaire();
        exemplaire.setLivre(livreOpt.get());
        model.addAttribute("livre", livreOpt.get());
        model.addAttribute("exemplaire", exemplaire);
        return "admin/exemplaires/form";
    }

    @PostMapping("/livre/{livreId}/create")
    public String createExemplaire(@PathVariable Long livreId,
                                   @Valid @ModelAttribute("exemplaire") Exemplaire exemplaire,
                                   BindingResult result,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        Optional<Livre> livreOpt = livreService.getLivreById(livreId);
        if (livreOpt.isEmpty()) {
            return "redirect:/livres";
        }
        if (exemplaireService.existsByCodeExemplaire(exemplaire.getCodeExemplaire())) {
            result.rejectValue("codeExemplaire", "duplicate", "Ce code exemplaire existe déjà");
        }
        if (result.hasErrors()) {
            model.addAttribute("livre", livreOpt.get());
            return "admin/exemplaires/form";
        }
        exemplaire.setLivre(livreOpt.get());
        exemplaireService.saveExemplaire(exemplaire);
        redirectAttributes.addFlashAttribute("success", "Exemplaire ajouté avec succès");
        return "redirect:/admin/exemplaires/livre/" + livreId;
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Exemplaire> exemplaireOpt = exemplaireService.getExemplaireById(id);
        if (exemplaireOpt.isEmpty()) {
            return "redirect:/livres";
        }
        Exemplaire exemplaire = exemplaireOpt.get();
        model.addAttribute("livre", exemplaire.getLivre());
        model.addAttribute("exemplaire", exemplaire);
        return "admin/exemplaires/form";
    }

    @PostMapping("/edit/{id}")
    public String updateExemplaire(@PathVariable Long id,
                                   @Valid @ModelAttribute("exemplaire") Exemplaire exemplaire,
                                   BindingResult result,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        Optional<Exemplaire> existingOpt = exemplaireService.getExemplaireById(id);
        if (existingOpt.isEmpty()) {
            return "redirect:/livres";
        }
        Exemplaire existing = existingOpt.get();
        if (!exemplaire.getCodeExemplaire().equals(existing.getCodeExemplaire()) && exemplaireService.existsByCodeExemplaire(exemplaire.getCodeExemplaire())) {
            result.rejectValue("codeExemplaire", "duplicate", "Ce code exemplaire existe déjà");
        }
        if (result.hasErrors()) {
            model.addAttribute("livre", existing.getLivre());
            return "admin/exemplaires/form";
        }
        exemplaire.setIdExemplaire(id);
        exemplaire.setLivre(existing.getLivre());
        exemplaireService.saveExemplaire(exemplaire);
        redirectAttributes.addFlashAttribute("success", "Exemplaire modifié avec succès");
        return "redirect:/admin/exemplaires/livre/" + existing.getLivre().getIdLivre();
    }

    @PostMapping("/delete/{id}")
    public String deleteExemplaire(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<Exemplaire> exemplaireOpt = exemplaireService.getExemplaireById(id);
        if (exemplaireOpt.isPresent()) {
            Long livreId = exemplaireOpt.get().getLivre().getIdLivre();
            exemplaireService.deleteExemplaire(id);
            redirectAttributes.addFlashAttribute("success", "Exemplaire supprimé avec succès");
            return "redirect:/admin/exemplaires/livre/" + livreId;
        }
        return "redirect:/livres";
    }
} 