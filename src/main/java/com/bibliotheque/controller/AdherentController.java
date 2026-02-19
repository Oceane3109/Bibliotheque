package com.bibliotheque.controller;

import com.bibliotheque.model.Adherent;
import com.bibliotheque.model.TypeAdherent;
import com.bibliotheque.model.User;
import com.bibliotheque.service.AdherentService;
import com.bibliotheque.service.TypeAdherentService;
import com.bibliotheque.service.UserService;
import com.bibliotheque.service.UserPenaliseService;
import com.bibliotheque.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Optional;

@Controller
@RequestMapping("/adherents")
public class AdherentController {

    private final AdherentService adherentService;
    private final UserService userService;
    private final TypeAdherentService typeAdherentService;
    private final UserPenaliseService userPenaliseService;
    private final NotificationService notificationService;

    @Autowired
    public AdherentController(AdherentService adherentService, 
                            UserService userService,
                            TypeAdherentService typeAdherentService,
                            UserPenaliseService userPenaliseService,
                            NotificationService notificationService) {
        this.adherentService = adherentService;
        this.userService = userService;
        this.typeAdherentService = typeAdherentService;
        this.userPenaliseService = userPenaliseService;
        this.notificationService = notificationService;
    }

    @GetMapping("/inscription")
    public String showInscriptionForm(Model model) {
        model.addAttribute("adherent", new Adherent());
        model.addAttribute("typesAdherents", typeAdherentService.getAllTypeAdherents());
        return "adherent/inscription";
    }

    @PostMapping("/inscription")
    public String inscrireAdherent(@Valid @ModelAttribute("adherent") Adherent adherent,
                                  BindingResult result,
                                  @RequestParam("typeAdherentId") Long typeAdherentId,
                                  Authentication authentication,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("typesAdherents", typeAdherentService.getAllTypeAdherents());
            return "adherent/inscription";
        }

        String username = authentication.getName();
        Optional<User> userOpt = userService.getUserByUsername(username);
        
        if (userOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Utilisateur non trouvé");
            return "redirect:/adherents/inscription";
        }

        if (adherentService.existsByEmail(adherent.getEmail())) {
            result.rejectValue("email", "duplicate", "Cet email est déjà utilisé");
            model.addAttribute("typesAdherents", typeAdherentService.getAllTypeAdherents());
            return "adherent/inscription";
        }

        Optional<TypeAdherent> typeAdherentOpt = typeAdherentService.getTypeAdherentById(typeAdherentId);
        if (typeAdherentOpt.isEmpty()) {
            result.rejectValue("typeAdherent", "invalid", "Type d'adhérent invalide");
            model.addAttribute("typesAdherents", typeAdherentService.getAllTypeAdherents());
            return "adherent/inscription";
        }

        adherent.setUser(userOpt.get());
        adherent.setTypeAdherent(typeAdherentOpt.get());
        adherent.setDateInscription(LocalDate.now());
        adherentService.saveAdherent(adherent);

        redirectAttributes.addFlashAttribute("success", "Inscription réussie !");
        return "redirect:/adherents/profile";
    }

    @GetMapping("/profile")
    public String showProfile(Authentication authentication, Model model) {
        String username = authentication.getName();
        Optional<User> userOpt = userService.getUserByUsername(username);
        
        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }

        Optional<Adherent> adherentOpt = adherentService.getAdherentByUser(userOpt.get());
        if (adherentOpt.isEmpty()) {
            return "redirect:/adherents/inscription";
        }

        Adherent adherent = adherentOpt.get();
        boolean isPenalise = userPenaliseService.isAdherentPenalise(adherent);
        int notificationsNonLuesCount = notificationService.getNotificationsNonLuesByAdherent(adherent).size();
        model.addAttribute("adherent", adherent);
        model.addAttribute("isPenalise", isPenalise);
        model.addAttribute("notificationsNonLuesCount", notificationsNonLuesCount);
        return "adherent/profile";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/list")
    public String listAdherents(Model model) {
        model.addAttribute("adherents", adherentService.getAllAdherents());
        return "admin/adherents/list";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Adherent> adherentOpt = adherentService.getAdherentById(id);
        if (adherentOpt.isEmpty()) {
            return "redirect:/adherents/admin/list";
        }

        model.addAttribute("adherent", adherentOpt.get());
        model.addAttribute("typesAdherents", typeAdherentService.getAllTypeAdherents());
        return "admin/adherents/edit";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/edit/{id}")
    public String updateAdherent(@PathVariable Long id,
                               @Valid @ModelAttribute("adherent") Adherent adherent,
                               BindingResult result,
                               @RequestParam("typeAdherentId") Long typeAdherentId,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("typesAdherents", typeAdherentService.getAllTypeAdherents());
            return "admin/adherents/edit";
        }

        Optional<Adherent> existingAdherentOpt = adherentService.getAdherentById(id);
        if (existingAdherentOpt.isEmpty()) {
            return "redirect:/adherents/admin/list";
        }

        Optional<TypeAdherent> typeAdherentOpt = typeAdherentService.getTypeAdherentById(typeAdherentId);
        if (typeAdherentOpt.isEmpty()) {
            result.rejectValue("typeAdherent", "invalid", "Type d'adhérent invalide");
            model.addAttribute("typesAdherents", typeAdherentService.getAllTypeAdherents());
            return "admin/adherents/edit";
        }

        Adherent existingAdherent = existingAdherentOpt.get();
        adherent.setIdAdherent(id);
        adherent.setUser(existingAdherent.getUser());
        adherent.setTypeAdherent(typeAdherentOpt.get());
        adherent.setDateInscription(existingAdherent.getDateInscription());

        adherentService.saveAdherent(adherent);
        redirectAttributes.addFlashAttribute("success", "Adhérent mis à jour avec succès");
        return "redirect:/adherents/admin/list";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/delete/{id}")
    public String deleteAdherent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<Adherent> adherentOpt = adherentService.getAdherentById(id);
        if (adherentOpt.isPresent()) {
            Adherent adherent = adherentOpt.get();
            if (adherent.getLivresEmpruntesDomicile() > 0 || adherent.getLivresEmpruntesSurplace() > 0) {
                redirectAttributes.addFlashAttribute("error", 
                    "Impossible de supprimer cet adhérent car il a des livres empruntés");
                return "redirect:/adherents/admin/list";
            }
            adherentService.deleteAdherent(id);
            redirectAttributes.addFlashAttribute("success", "Adhérent supprimé avec succès");
        }
        return "redirect:/adherents/admin/list";
    }


} 