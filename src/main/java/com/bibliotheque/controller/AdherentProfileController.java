package com.bibliotheque.controller;

import com.bibliotheque.model.Adherent;
import com.bibliotheque.service.AdherentService;
import com.bibliotheque.service.NotificationService;
import com.bibliotheque.service.AbonnementService;
import com.bibliotheque.model.Abonnement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/adherent")
public class AdherentProfileController {

    private final AdherentService adherentService;
    private final NotificationService notificationService;
    private final AbonnementService abonnementService;

    @Autowired
    public AdherentProfileController(AdherentService adherentService, 
                                   NotificationService notificationService,
                                   AbonnementService abonnementService) {
        this.adherentService = adherentService;
        this.notificationService = notificationService;
        this.abonnementService = abonnementService;
    }

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) return "redirect:/login";
        
        String username = userDetails.getUsername();
        Optional<Adherent> adherentOpt = adherentService.getAdherentByUserUsernameWithPrets(username);
        if (adherentOpt.isEmpty()) return "redirect:/login";
        
        Adherent adherent = adherentOpt.get();
        
        // Vérifier si l'adhérent est pénalisé
        boolean isPenalise = adherentService.isPenalise(adherent.getIdAdherent());
        adherent.setPenalise(isPenalise);
        
        var pretsActifs = adherent.getPrets().stream().filter(pret -> "en_cours".equals(pret.getEtatPret())).toList();
        long nbDomicileActifs = pretsActifs.stream().filter(pret -> pret.getTypePret() != null && pret.getTypePret().getNomTypePret().equalsIgnoreCase("domicile")).count();
        long nbSurPlaceActifs = pretsActifs.stream().filter(pret -> pret.getTypePret() != null && (pret.getTypePret().getNomTypePret().equalsIgnoreCase("sur_place") || pret.getTypePret().getNomTypePret().equalsIgnoreCase("sur place"))).count();
        double pourcentageDomicile = adherent.getMaxLivresDomicile() > 0 ? (double) nbDomicileActifs / adherent.getMaxLivresDomicile() * 100 : 0;
        double pourcentageSurplace = adherent.getMaxLivresSurplace() > 0 ? (double) nbSurPlaceActifs / adherent.getMaxLivresSurplace() * 100 : 0;
        // Historique total (tous prêts)
        var tousPrets = adherent.getPrets();
        long totalDomicile = tousPrets.stream().filter(pret -> pret.getTypePret() != null && pret.getTypePret().getNomTypePret().equalsIgnoreCase("domicile")).count();
        long totalSurPlace = tousPrets.stream().filter(pret -> pret.getTypePret() != null && (pret.getTypePret().getNomTypePret().equalsIgnoreCase("sur_place") || pret.getTypePret().getNomTypePret().equalsIgnoreCase("sur place"))).count();
        model.addAttribute("nbDomicileActifs", nbDomicileActifs);
        model.addAttribute("nbSurPlaceActifs", nbSurPlaceActifs);
        model.addAttribute("totalDomicile", totalDomicile);
        model.addAttribute("totalSurPlace", totalSurPlace);
        model.addAttribute("pourcentageDomicile", pourcentageDomicile);
        model.addAttribute("pourcentageSurplace", pourcentageSurplace);
        
        // Ajout de l'abonnement actif ou en attente dans le modèle
        Abonnement abonnement = abonnementService.getAbonnementActifByAdherent(adherent)
            .orElseGet(() -> abonnementService.getAbonnementsByAdherent(adherent).stream()
                .filter(a -> "en_attente".equals(a.getStatut()))
                .findFirst().orElse(null));
        model.addAttribute("abonnement", abonnement);

        model.addAttribute("adherent", adherent);
        model.addAttribute("notificationsNonLuesCount", 
                          notificationService.getNotificationsNonLuesByAdherent(adherent).size());
        
        return "adherent/profile";
    }

    @GetMapping("/profile/edit")
    public String editProfileForm(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) return "redirect:/login";
        String username = userDetails.getUsername();
        Optional<Adherent> adherentOpt = adherentService.getAdherentByUserUsername(username);
        if (adherentOpt.isEmpty()) return "redirect:/login";
        model.addAttribute("adherent", adherentOpt.get());
        return "adherent/edit-profile";
    }

    @PostMapping("/profile/edit")
    public String editProfileSubmit(@AuthenticationPrincipal UserDetails userDetails,
                               @ModelAttribute Adherent adherent,
                               RedirectAttributes redirectAttributes) {
        if (userDetails == null) return "redirect:/login";
        String username = userDetails.getUsername();
        Optional<Adherent> adherentOpt = adherentService.getAdherentByUserUsername(username);
        if (adherentOpt.isEmpty()) return "redirect:/login";
        Adherent existing = adherentOpt.get();
        // On ne permet de modifier que certains champs (nom, prénom, email, téléphone, adresse)
        existing.setNom(adherent.getNom());
        existing.setPrenom(adherent.getPrenom());
        existing.setEmail(adherent.getEmail());
        existing.setTelephone(adherent.getTelephone());
        existing.setAdresse(adherent.getAdresse());
        adherentService.saveAdherent(existing);
        redirectAttributes.addFlashAttribute("success", "Profil mis à jour avec succès.");
        return "redirect:/adherent/profile";
    }
} 