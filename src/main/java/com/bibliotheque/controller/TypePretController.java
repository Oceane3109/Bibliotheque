package com.bibliotheque.controller;

import com.bibliotheque.model.TypePret;
import com.bibliotheque.service.TypePretService;
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
@RequestMapping("/admin/types-pret")
@PreAuthorize("hasRole('ADMIN')")
public class TypePretController {
    private final TypePretService typePretService;

    @Autowired
    public TypePretController(TypePretService typePretService) {
        this.typePretService = typePretService;
    }

    @GetMapping
    public String listTypesPret(Model model) {
        model.addAttribute("typesPret", typePretService.getAllTypePrets());
        return "admin/types-pret/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("typePret", new TypePret());
        return "admin/types-pret/form";
    }

    @PostMapping("/create")
    public String createTypePret(@Valid @ModelAttribute("typePret") TypePret typePret,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes) {
        if (typePretService.existsByNomTypePret(typePret.getNomTypePret())) {
            result.rejectValue("nomTypePret", "duplicate", "Ce nom de type de prêt existe déjà");
        }
        if (result.hasErrors()) {
            return "admin/types-pret/form";
        }
        typePretService.saveTypePret(typePret);
        redirectAttributes.addFlashAttribute("success", "Type de prêt ajouté avec succès");
        return "redirect:/admin/types-pret";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<TypePret> typePretOpt = typePretService.getTypePretById(id);
        if (typePretOpt.isEmpty()) {
            return "redirect:/admin/types-pret";
        }
        model.addAttribute("typePret", typePretOpt.get());
        return "admin/types-pret/form";
    }

    @PostMapping("/edit/{id}")
    public String updateTypePret(@PathVariable Long id,
                                 @Valid @ModelAttribute("typePret") TypePret typePret,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes) {
        Optional<TypePret> existingOpt = typePretService.getTypePretById(id);
        if (existingOpt.isEmpty()) {
            return "redirect:/admin/types-pret";
        }
        if (!typePret.getNomTypePret().equals(existingOpt.get().getNomTypePret()) && typePretService.existsByNomTypePret(typePret.getNomTypePret())) {
            result.rejectValue("nomTypePret", "duplicate", "Ce nom de type de prêt existe déjà");
        }
        if (result.hasErrors()) {
            return "admin/types-pret/form";
        }
        typePret.setIdTypePret(id);
        typePretService.saveTypePret(typePret);
        redirectAttributes.addFlashAttribute("success", "Type de prêt modifié avec succès");
        return "redirect:/admin/types-pret";
    }

    @PostMapping("/delete/{id}")
    public String deleteTypePret(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        typePretService.deleteTypePret(id);
        redirectAttributes.addFlashAttribute("success", "Type de prêt supprimé avec succès");
        return "redirect:/admin/types-pret";
    }
} 