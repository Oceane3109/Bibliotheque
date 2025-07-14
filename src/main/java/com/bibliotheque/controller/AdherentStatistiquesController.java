package com.bibliotheque.controller;

import com.bibliotheque.model.Adherent;
import com.bibliotheque.service.AdherentService;
import com.bibliotheque.service.PretLivreService;
import com.bibliotheque.service.ReservationService;
import com.bibliotheque.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.Optional;

@Controller
@RequestMapping("/adherent")
public class AdherentStatistiquesController {

    private final AdherentService adherentService;
    private final PretLivreService pretLivreService;
    private final ReservationService reservationService;
    private final NotificationService notificationService;

    @Autowired
    public AdherentStatistiquesController(AdherentService adherentService,
                                        PretLivreService pretLivreService,
                                        ReservationService reservationService,
                                        NotificationService notificationService) {
        this.adherentService = adherentService;
        this.pretLivreService = pretLivreService;
        this.reservationService = reservationService;
        this.notificationService = notificationService;
    }

    @GetMapping("/statistiques")
    public String statistiques(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) return "redirect:/login";
        
        String username = userDetails.getUsername();
        Optional<Adherent> adherentOpt = adherentService.getAdherentByUserUsername(username);
        if (adherentOpt.isEmpty()) return "redirect:/login";
        
        Adherent adherent = adherentOpt.get();
        
        // Vérifier si l'adhérent est pénalisé
        boolean isPenalise = adherentService.isPenalise(adherent.getIdAdherent());
        adherent.setPenalise(isPenalise);
        
        // Statistiques des prêts
        var pretsActifs = pretLivreService.getPretsByAdherentAndEtat(adherent, "en_cours");
        var pretsEnRetard = pretLivreService.getPretsByAdherentAndEtat(adherent, "en_retard");
        var pretsTermines = pretLivreService.getPretsByAdherentAndEtat(adherent, "retourne");
        
        // Statistiques des réservations
        var reservationsEnAttente = reservationService.getReservationsByAdherentAndEtat(adherent, "en_attente");
        var reservationsConfirmees = reservationService.getReservationsByAdherentAndEtat(adherent, "confirmee");
        
        // Calculs des pourcentages
        long nbDomicileActifs = pretsActifs.stream().filter(pret -> pret.getTypePret() != null && pret.getTypePret().getNomTypePret().equalsIgnoreCase("domicile")).count();
        long nbSurPlaceActifs = pretsActifs.stream().filter(pret -> pret.getTypePret() != null && (pret.getTypePret().getNomTypePret().equalsIgnoreCase("sur_place") || pret.getTypePret().getNomTypePret().equalsIgnoreCase("sur place"))).count();
        double pourcentageDomicile = adherent.getMaxLivresDomicile() > 0 ? (double) nbDomicileActifs / adherent.getMaxLivresDomicile() * 100 : 0;
        double pourcentageSurplace = adherent.getMaxLivresSurplace() > 0 ? (double) nbSurPlaceActifs / adherent.getMaxLivresSurplace() * 100 : 0;
        // Historique total (tous prêts)
        var tousPrets = pretLivreService.getPretsByAdherent(adherent);
        long totalDomicile = tousPrets.stream().filter(pret -> pret.getTypePret() != null && pret.getTypePret().getNomTypePret().equalsIgnoreCase("domicile")).count();
        long totalSurPlace = tousPrets.stream().filter(pret -> pret.getTypePret() != null && (pret.getTypePret().getNomTypePret().equalsIgnoreCase("sur_place") || pret.getTypePret().getNomTypePret().equalsIgnoreCase("sur place"))).count();
        
        // Statistiques temporelles
        int pretsCeMois = (int) pretsActifs.stream()
            .filter(pret -> pret.getDateDebut().getMonth() == LocalDate.now().getMonth())
            .count();
        
        int pretsCetteAnnee = (int) pretsActifs.stream()
            .filter(pret -> pret.getDateDebut().getYear() == LocalDate.now().getYear())
            .count();
        
        model.addAttribute("adherent", adherent);
        model.addAttribute("pretsActifs", pretsActifs);
        model.addAttribute("pretsEnRetard", pretsEnRetard);
        model.addAttribute("pretsTermines", pretsTermines);
        model.addAttribute("reservationsEnAttente", reservationsEnAttente);
        model.addAttribute("reservationsConfirmees", reservationsConfirmees);
        model.addAttribute("pourcentageDomicile", pourcentageDomicile);
        model.addAttribute("pourcentageSurplace", pourcentageSurplace);
        model.addAttribute("pretsCeMois", pretsCeMois);
        model.addAttribute("pretsCetteAnnee", pretsCetteAnnee);
        model.addAttribute("notificationsNonLuesCount", 
                          notificationService.getNotificationsNonLuesByAdherent(adherent).size());
        model.addAttribute("nbDomicileActifs", nbDomicileActifs);
        model.addAttribute("nbSurPlaceActifs", nbSurPlaceActifs);
        model.addAttribute("totalDomicile", totalDomicile);
        model.addAttribute("totalSurPlace", totalSurPlace);
        
        return "adherent/statistiques";
    }
} 