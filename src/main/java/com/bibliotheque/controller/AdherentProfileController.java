package com.bibliotheque.controller;

import com.bibliotheque.model.Adherent;
import com.bibliotheque.service.AdherentService;
import com.bibliotheque.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/adherent")
public class AdherentProfileController {

    private final AdherentService adherentService;
    private final NotificationService notificationService;

    @Autowired
    public AdherentProfileController(AdherentService adherentService, 
                                   NotificationService notificationService) {
        this.adherentService = adherentService;
        this.notificationService = notificationService;
    }

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) return "redirect:/login";
        
        String username = userDetails.getUsername();
        Optional<Adherent> adherentOpt = adherentService.getAdherentByUserUsername(username);
        if (adherentOpt.isEmpty()) return "redirect:/login";
        
        Adherent adherent = adherentOpt.get();
        
        // Vérifier si l'adhérent est pénalisé
        boolean isPenalise = adherentService.isPenalise(adherent.getIdAdherent());
        adherent.setPenalise(isPenalise);
        
        // Calculer les pourcentages pour les barres de progression
        double pourcentageDomicile = adherent.getMaxLivresDomicile() > 0 ? 
            (double) adherent.getLivresEmpruntesDomicile() / adherent.getMaxLivresDomicile() * 100 : 0;
        double pourcentageSurplace = adherent.getMaxLivresSurplace() > 0 ? 
            (double) adherent.getLivresEmpruntesSurplace() / adherent.getMaxLivresSurplace() * 100 : 0;
        
        model.addAttribute("adherent", adherent);
        model.addAttribute("pourcentageDomicile", pourcentageDomicile);
        model.addAttribute("pourcentageSurplace", pourcentageSurplace);
        model.addAttribute("notificationsNonLuesCount", 
                          notificationService.getNotificationsNonLuesByAdherent(adherent).size());
        
        return "adherent/profile";
    }
} 