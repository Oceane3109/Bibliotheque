package com.bibliotheque.controller;

import com.bibliotheque.model.Notification;
import com.bibliotheque.model.Adherent;
import com.bibliotheque.service.NotificationService;
import com.bibliotheque.service.AdherentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class NotificationController {
    private final NotificationService notificationService;
    private final AdherentService adherentService;

    @Autowired
    public NotificationController(NotificationService notificationService, AdherentService adherentService) {
        this.notificationService = notificationService;
        this.adherentService = adherentService;
    }

    @PostMapping("/notification/lue/{id}")
    public String marquerCommeLue(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        notificationService.marquerCommeLue(id);
        redirectAttributes.addFlashAttribute("success", "Notification marquée comme lue.");
        return "redirect:/adherent/mes-notifications";
    }
} 