package com.bibliotheque.controller;

import com.bibliotheque.model.TypeAdherent;
import com.bibliotheque.service.TypeAdherentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/types-adherents")
@PreAuthorize("hasRole('ADMIN')")
public class TypeAdherentController {

    private final TypeAdherentService typeAdherentService;

    @Autowired
    public TypeAdherentController(TypeAdherentService typeAdherentService) {
        this.typeAdherentService = typeAdherentService;
    }

    @GetMapping
    public String listTypeAdherents(Model model) {
        model.addAttribute("types", typeAdherentService.getAllTypeAdherents());
        model.addAttribute("newType", new TypeAdherent());
        return "admin/types-adherents/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("typeAdherent", new TypeAdherent());
        return "admin/types-adherents/form";
    }

    @PostMapping("/create")
    public String createTypeAdherent(@Valid @ModelAttribute("typeAdherent") TypeAdherent typeAdherent,
                                   BindingResult result,
                                   Model model) {
        if (result.hasErrors()) {
            return "admin/types-adherents/form";
        }

        if (typeAdherentService.existsByNomType(typeAdherent.getNomType())) {
            result.rejectValue("nomType", "duplicate", "Ce type d'adhérent existe déjà");
            return "admin/types-adherents/form";
        }

        typeAdherentService.saveTypeAdherent(typeAdherent);
        return "redirect:/admin/types-adherents";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        TypeAdherent typeAdherent = typeAdherentService.getTypeAdherentById(id)
                .orElseThrow(() -> new IllegalArgumentException("Type d'adhérent invalide:" + id));
        model.addAttribute("typeAdherent", typeAdherent);
        return "admin/types-adherents/form";
    }

    @PostMapping("/edit/{id}")
    public String updateTypeAdherent(@PathVariable Long id,
                                   @Valid @ModelAttribute("typeAdherent") TypeAdherent typeAdherent,
                                   BindingResult result) {
        if (result.hasErrors()) {
            return "admin/types-adherents/form";
        }

        typeAdherent.setIdTypeAdherent(id);
        typeAdherentService.saveTypeAdherent(typeAdherent);
        return "redirect:/admin/types-adherents";
    }

    @PostMapping("/delete/{id}")
    public String deleteTypeAdherent(@PathVariable Long id) {
        typeAdherentService.deleteTypeAdherent(id);
        return "redirect:/admin/types-adherents";
    }
} 