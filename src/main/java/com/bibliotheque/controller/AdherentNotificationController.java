package com.bibliotheque.controller;

import com.bibliotheque.model.Adherent;
import com.bibliotheque.model.Notification;
import com.bibliotheque.service.AdherentService;
import com.bibliotheque.service.NotificationService;
import com.bibliotheque.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/adherent")
public class AdherentNotificationController {

    private final NotificationService notificationService;
    private final AdherentService adherentService;
    private final UserService userService;

    @Autowired
    public AdherentNotificationController(NotificationService notificationService, 
                                        AdherentService adherentService,
                                        UserService userService) {
        this.notificationService = notificationService;
        this.adherentService = adherentService;
        this.userService = userService;
    }

    @GetMapping("/mes-notifications")
    public String mesNotifications(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) return "redirect:/login";
        
        String username = userDetails.getUsername();
        Optional<Adherent> adherentOpt = adherentService.getAdherentByUserUsername(username);
        if (adherentOpt.isEmpty()) return "redirect:/login";
        
        Adherent adherent = adherentOpt.get();
        List<Notification> notifications = notificationService.getNotificationsByAdherent(adherent);
        
        model.addAttribute("notifications", notifications);
        model.addAttribute("notificationsNonLuesCount", 
                          notificationService.getNotificationsNonLuesByAdherent(adherent).size());
        
        return "adherent/mes-notifications";
    }
} 