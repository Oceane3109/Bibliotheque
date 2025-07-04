package com.bibliotheque.controller;

import com.bibliotheque.model.Adherent;
import com.bibliotheque.service.AdherentService;
import com.bibliotheque.service.TypeAdherentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin/adherents")
@PreAuthorize("hasRole('ADMIN')")
public class AdminAdherentController {

    private final AdherentService adherentService;
    private final TypeAdherentService typeAdherentService;

    @Autowired
    public AdminAdherentController(AdherentService adherentService, TypeAdherentService typeAdherentService) {
        this.adherentService = adherentService;
        this.typeAdherentService = typeAdherentService;
    }

    @GetMapping("/list")
    public String listAdherents(Model model) {
        List<Adherent> adherents = adherentService.getAllAdherents();
        model.addAttribute("adherents", adherents);
        return "admin/adherents/list";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@org.springframework.web.bind.annotation.PathVariable Long id, org.springframework.ui.Model model) {
        var adherentOpt = adherentService.getAdherentById(id);
        if (adherentOpt.isEmpty()) {
            return "redirect:/admin/adherents/list";
        }
        model.addAttribute("adherent", adherentOpt.get());
        model.addAttribute("typeAdherents", typeAdherentService.getAllTypeAdherents());
        return "admin/adherents/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateAdherent(@org.springframework.web.bind.annotation.PathVariable Long id,
                                 @org.springframework.web.bind.annotation.ModelAttribute Adherent adherent,
                                 org.springframework.validation.BindingResult result,
                                 org.springframework.ui.Model model,
                                 org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("adherent", adherent);
            model.addAttribute("typeAdherents", typeAdherentService.getAllTypeAdherents());
            return "admin/adherents/edit";
        }
        var existingOpt = adherentService.getAdherentById(id);
        if (existingOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Adhérent introuvable.");
            return "redirect:/admin/adherents/list";
        }
        Adherent existing = existingOpt.get();
        adherent.setIdAdherent(id);
        adherent.setUser(existing.getUser());
        adherent.setDateInscription(existing.getDateInscription());
        if (adherent.getTypeAdherent() != null && adherent.getTypeAdherent().getIdTypeAdherent() != null) {
            typeAdherentService.getTypeAdherentById(adherent.getTypeAdherent().getIdTypeAdherent())
                .ifPresent(adherent::setTypeAdherent);
        }
        adherentService.saveAdherent(adherent);
        redirectAttributes.addFlashAttribute("success", "Adhérent modifié avec succès");
        return "redirect:/admin/adherents/list";
    }
} 