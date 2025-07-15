package com.bibliotheque.controller;

import com.bibliotheque.model.Livre;
import com.bibliotheque.service.LivreService;
import com.bibliotheque.service.UserService;
import com.bibliotheque.service.AdherentService;
import com.bibliotheque.service.NoteLivreService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/livres")
public class LivreController {

    private final LivreService livreService;
    private final UserService userService;
    private final AdherentService adherentService;
    private final NoteLivreService noteLivreService;

    @Autowired
    public LivreController(LivreService livreService, UserService userService, AdherentService adherentService, NoteLivreService noteLivreService) {
        this.livreService = livreService;
        this.userService = userService;
        this.adherentService = adherentService;
        this.noteLivreService = noteLivreService;
    }

    @GetMapping
    public String listLivres(Model model) {
        model.addAttribute("livres", livreService.getAllLivres());
        return "livre/list";
    }

    @GetMapping("/search")
    public String searchLivres(@RequestParam(required = false) String titre,
                             @RequestParam(required = false) String auteur,
                             Model model) {
        if (titre != null && !titre.isEmpty()) {
            model.addAttribute("livres", livreService.getLivresByTitre(titre));
        } else if (auteur != null && !auteur.isEmpty()) {
            model.addAttribute("livres", livreService.getLivresByAuteur(auteur));
        } else {
            model.addAttribute("livres", livreService.getAllLivres());
        }
        return "livre/list";
    }

    @GetMapping("/{id}")
    public String showLivre(@PathVariable Long id, Model model) {
        Optional<Livre> livreOpt = livreService.getLivreById(id);
        if (livreOpt.isEmpty()) {
            return "redirect:/livres";
        }
        model.addAttribute("livre", livreOpt.get());
        return "livre/detail";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/create")
    public String showCreateForm(Model model) {
        model.addAttribute("livre", new Livre());
        return "admin/livres/form";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/create")
    public String createLivre(@Valid @ModelAttribute("livre") Livre livre,
                            BindingResult result,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (livre.getIsbn() != null && !livre.getIsbn().isEmpty() && livreService.existsByIsbn(livre.getIsbn())) {
            result.rejectValue("isbn", "duplicate", "Cet ISBN est déjà utilisé");
        }

        if (result.hasErrors()) {
            return "admin/livres/form";
        }

        try {
            livreService.saveLivre(livre);
            redirectAttributes.addFlashAttribute("success", "Livre ajouté avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'ajout du livre");
            return "admin/livres/form";
        }

        return "redirect:/livres/admin/list";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Livre> livreOpt = livreService.getLivreById(id);
        if (livreOpt.isEmpty()) {
            return "redirect:/livres/admin/list";
        }
        model.addAttribute("livre", livreOpt.get());
        return "admin/livres/form";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/edit/{id}")
    public String updateLivre(@PathVariable Long id,
                            @Valid @ModelAttribute("livre") Livre livre,
                            BindingResult result,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        Optional<Livre> existingLivreOpt = livreService.getLivreById(id);
        if (existingLivreOpt.isEmpty()) {
            return "redirect:/livres/admin/list";
        }

        if (livre.getIsbn() != null && !livre.getIsbn().isEmpty()) {
            Optional<Livre> livreWithIsbn = livreService.getLivreByIsbn(livre.getIsbn());
            if (livreWithIsbn.isPresent() && !livreWithIsbn.get().getIdLivre().equals(id)) {
                result.rejectValue("isbn", "duplicate", "Cet ISBN est déjà utilisé");
            }
        }

        if (result.hasErrors()) {
            return "admin/livres/form";
        }

        livre.setIdLivre(id);
        // Si l'URL de l'image est vide, on conserve l'ancienne valeur
        if (livre.getImageUrl() == null || livre.getImageUrl().isEmpty()) {
            Livre existingLivre = existingLivreOpt.get();
            livre.setImageUrl(existingLivre.getImageUrl());
        }
        try {
            livreService.saveLivre(livre);
            redirectAttributes.addFlashAttribute("success", "Livre mis à jour avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la mise à jour du livre");
            return "admin/livres/form";
        }

        return "redirect:/livres/admin/list";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/delete/{id}")
    public String deleteLivre(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<Livre> livreOpt = livreService.getLivreById(id);
        if (livreOpt.isPresent()) {
            Livre livre = livreOpt.get();
            if (!livre.getExemplaires().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", 
                    "Impossible de supprimer ce livre car il a des exemplaires");
                return "redirect:/livres/admin/list";
            }
            livreService.deleteLivre(id);
            redirectAttributes.addFlashAttribute("success", "Livre supprimé avec succès");
        }
        return "redirect:/livres/admin/list";
    }

    // Je supprime la méthode getImage (endpoint /image/{id}) et deleteImage car on ne gère plus d'image binaire

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/list")
    public String adminListLivres(Model model) {
        model.addAttribute("livres", livreService.getAllLivresWithExemplaires());
        return "admin/livres/list";
    }
} 