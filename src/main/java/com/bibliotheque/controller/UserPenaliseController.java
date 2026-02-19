package com.bibliotheque.controller;

import com.bibliotheque.model.UserPenalise;
import com.bibliotheque.model.Adherent;
import com.bibliotheque.service.UserPenaliseService;
import com.bibliotheque.service.AdherentService;
import com.bibliotheque.service.NotificationService;
import com.bibliotheque.model.Notification;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;
import java.util.List;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/admin/users-penalises")
@PreAuthorize("hasRole('ADMIN')")
public class UserPenaliseController {
    private final UserPenaliseService userPenaliseService;
    private final AdherentService adherentService;
    private final NotificationService notificationService;

    @Autowired
    public UserPenaliseController(UserPenaliseService userPenaliseService, AdherentService adherentService, NotificationService notificationService) {
        this.userPenaliseService = userPenaliseService;
        this.adherentService = adherentService;
        this.notificationService = notificationService;
    }

    @GetMapping
    public String listUserPenalises(Model model) {
        List<UserPenalise> penalises = userPenaliseService.getAllUserPenalises();
        model.addAttribute("penalises", penalises);
        return "admin/users-penalises/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("userPenalise", new UserPenalise());
        model.addAttribute("adherents", adherentService.getAllAdherents());
        return "admin/users-penalises/form";
    }

    @PostMapping("/create")
    public String createUserPenalise(@Valid @ModelAttribute("userPenalise") UserPenalise userPenalise,
                                     BindingResult result,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("adherents", adherentService.getAllAdherents());
            return "admin/users-penalises/form";
        }
        userPenaliseService.saveUserPenalise(userPenalise);
        // Notification automatique
        Notification notif = new Notification();
        notif.setAdherent(userPenalise.getAdherent());
        notif.setTitre("Suspension de compte");
        notif.setMessage("Votre compte est suspendu du " + userPenalise.getDateDebut() + " au " + userPenalise.getDateFin() + ". Motif : " + (userPenalise.getMotif() != null ? userPenalise.getMotif() : "Non précisé"));
        notif.setDateEnvoi(LocalDateTime.now());
        notif.setLu(false);
        notificationService.saveNotification(notif);
        redirectAttributes.addFlashAttribute("success", "Suspension enregistrée avec succès");
        return "redirect:/admin/users-penalises";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<UserPenalise> penaliseOpt = userPenaliseService.getUserPenaliseById(id);
        if (penaliseOpt.isEmpty()) {
            return "redirect:/admin/users-penalises";
        }
        model.addAttribute("userPenalise", penaliseOpt.get());
        model.addAttribute("adherents", adherentService.getAllAdherents());
        return "admin/users-penalises/form";
    }

    @PostMapping("/edit/{id}")
    public String updateUserPenalise(@PathVariable Long id,
                                     @Valid @ModelAttribute("userPenalise") UserPenalise userPenalise,
                                     BindingResult result,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("adherents", adherentService.getAllAdherents());
            return "admin/users-penalises/form";
        }
        userPenalise.setIdUserPenalise(id);
        userPenaliseService.saveUserPenalise(userPenalise);
        redirectAttributes.addFlashAttribute("success", "Suspension modifiée avec succès");
        return "redirect:/admin/users-penalises";
    }

    @PostMapping("/delete/{id}")
    public String deleteUserPenalise(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userPenaliseService.deleteUserPenalise(id);
        redirectAttributes.addFlashAttribute("success", "Suspension supprimée avec succès");
        return "redirect:/admin/users-penalises";
    }

    @PostMapping("/desactiver-expirees")
    public String desactiverSuspensionsExpirees(RedirectAttributes redirectAttributes) {
        userPenaliseService.desactiverSuspensionsExpirees();
        redirectAttributes.addFlashAttribute("success", "Les suspensions expirées ont été désactivées.");
        return "redirect:/admin/users-penalises";
    }
} 