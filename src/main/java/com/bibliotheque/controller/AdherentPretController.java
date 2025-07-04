package com.bibliotheque.controller;

import com.bibliotheque.model.Adherent;
import com.bibliotheque.model.PretLivre;
import com.bibliotheque.model.ProlongementPret;
import com.bibliotheque.model.Livre;
import com.bibliotheque.model.Exemplaire;
import com.bibliotheque.model.Reservation;
import com.bibliotheque.model.TypePret;
import com.bibliotheque.service.AdherentService;
import com.bibliotheque.service.PretLivreService;
import com.bibliotheque.service.UserService;
import com.bibliotheque.service.ProlongementPretService;
import com.bibliotheque.service.NotificationService;
import com.bibliotheque.service.LivreService;
import com.bibliotheque.service.ExemplaireService;
import com.bibliotheque.service.ReservationService;
import com.bibliotheque.service.TypePretService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

@Controller
public class AdherentPretController {
    private final AdherentService adherentService;
    private final PretLivreService pretLivreService;
    private final UserService userService;
    private final ProlongementPretService prolongementPretService;
    private final NotificationService notificationService;
    private final LivreService livreService;
    private final ExemplaireService exemplaireService;
    private final ReservationService reservationService;
    private final TypePretService typePretService;

    @Autowired
    public AdherentPretController(AdherentService adherentService, 
                                PretLivreService pretLivreService, 
                                UserService userService, 
                                ProlongementPretService prolongementPretService, 
                                NotificationService notificationService,
                                LivreService livreService,
                                ExemplaireService exemplaireService,
                                ReservationService reservationService,
                                TypePretService typePretService) {
        this.adherentService = adherentService;
        this.pretLivreService = pretLivreService;
        this.userService = userService;
        this.prolongementPretService = prolongementPretService;
        this.notificationService = notificationService;
        this.livreService = livreService;
        this.exemplaireService = exemplaireService;
        this.reservationService = reservationService;
        this.typePretService = typePretService;
    }

    @GetMapping("/mes-prets")
    public String mesPrets(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) return "redirect:/login";
        String username = userDetails.getUsername();
        var userOpt = userService.getUserByUsername(username);
        if (userOpt.isEmpty()) return "redirect:/login";
        var adherentOpt = adherentService.getAdherentByUser(userOpt.get());
        if (adherentOpt.isEmpty()) return "redirect:/login";
        Adherent adherent = adherentOpt.get();
        int notificationsNonLuesCount = notificationService.getNotificationsNonLuesByAdherent(adherent).size();

        // Prêts actifs de l'adhérent
        List<PretLivre> pretsActifs = pretLivreService.getPretsByAdherentAndEtat(adherent, "actif");
        List<PretLivre> pretsRetard = pretLivreService.getPretsByAdherentAndEtat(adherent, "retard");

        // Compteurs
        int maxDomicile = adherent.getMaxLivresDomicile();
        int maxSurplace = adherent.getMaxLivresSurplace();
        int empruntesDomicile = adherent.getLivresEmpruntesDomicile();
        int empruntesSurplace = adherent.getLivresEmpruntesSurplace();
        int restantDomicile = maxDomicile - empruntesDomicile;
        int restantSurplace = maxSurplace - empruntesSurplace;

        // Calcul du temps restant pour chaque prêt
        var pretsAvecTempsRestant = pretsActifs.stream().map(pret -> {
            long joursRestant = ChronoUnit.DAYS.between(LocalDate.now(), pret.getDateFin());
            return new Object[] { pret, joursRestant };
        }).collect(Collectors.toList());

        // Règles métier pour les prolongements
        Map<Long, String> etatProlongementParPret = new HashMap<>();
        for (PretLivre pret : pretsActifs) {
            var prolongements = prolongementPretService.getProlongementsByPret(pret);
            if (!prolongements.isEmpty()) {
                // On prend le dernier (par dateDemande)
                var dernier = prolongements.stream().max(java.util.Comparator.comparing(p -> p.getDateDemande())).get();
                etatProlongementParPret.put(pret.getIdPret(), dernier.getEtatProlongation());
            }
        }

        model.addAttribute("adherent", adherent);
        model.addAttribute("pretsActifs", pretsActifs);
        model.addAttribute("pretsRetard", pretsRetard);
        model.addAttribute("pretsAvecTempsRestant", pretsAvecTempsRestant);
        model.addAttribute("restantDomicile", restantDomicile);
        model.addAttribute("restantSurplace", restantSurplace);
        model.addAttribute("empruntesDomicile", empruntesDomicile);
        model.addAttribute("empruntesSurplace", empruntesSurplace);
        model.addAttribute("maxDomicile", maxDomicile);
        model.addAttribute("maxSurplace", maxSurplace);
        model.addAttribute("etatProlongementParPret", etatProlongementParPret);
        model.addAttribute("notificationsNonLuesCount", notificationsNonLuesCount);
        return "adherent/mes-prets";
    }

    @GetMapping("/mes-prolongements")
    public String mesProlongements(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) return "redirect:/login";
        String username = userDetails.getUsername();
        var userOpt = userService.getUserByUsername(username);
        if (userOpt.isEmpty()) return "redirect:/login";
        var adherentOpt = adherentService.getAdherentByUser(userOpt.get());
        if (adherentOpt.isEmpty()) return "redirect:/login";
        Adherent adherent = adherentOpt.get();
        int notificationsNonLuesCount = notificationService.getNotificationsNonLuesByAdherent(adherent).size();
        List<ProlongementPret> prolongements = prolongementPretService.getProlongementsByAdherent(adherent);
        model.addAttribute("prolongements", prolongements);
        model.addAttribute("notificationsNonLuesCount", notificationsNonLuesCount);
        return "adherent/mes-prolongements";
    }

    @GetMapping("/demande-prolongement/{pretId}")
    public String showDemandeProlongement(@PathVariable Long pretId, @AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) return "redirect:/login";
        String username = userDetails.getUsername();
        var userOpt = userService.getUserByUsername(username);
        if (userOpt.isEmpty()) return "redirect:/login";
        var adherentOpt = adherentService.getAdherentByUser(userOpt.get());
        if (adherentOpt.isEmpty()) return "redirect:/login";
        Adherent adherent = adherentOpt.get();
        int notificationsNonLuesCount = notificationService.getNotificationsNonLuesByAdherent(adherent).size();
        var pretOpt = pretLivreService.getPretById(pretId);
        ProlongementPret prolongement = new ProlongementPret();
        prolongement.setPretLivre(pretOpt.get());
        model.addAttribute("prolongementPret", prolongement);
        model.addAttribute("notificationsNonLuesCount", notificationsNonLuesCount);
        return "adherent/demande-prolongement";
    }

    @PostMapping("/demande-prolongement/{pretId}")
    public String submitDemandeProlongement(@PathVariable Long pretId, @ModelAttribute("prolongementPret") ProlongementPret prolongementPret, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        if (userDetails == null) return "redirect:/login";
        String username = userDetails.getUsername();
        var userOpt = userService.getUserByUsername(username);
        if (userOpt.isEmpty()) return "redirect:/login";
        var adherentOpt = adherentService.getAdherentByUser(userOpt.get());
        if (adherentOpt.isEmpty()) return "redirect:/login";
        Adherent adherent = adherentOpt.get();
        var pretOpt = pretLivreService.getPretById(pretId);
        if (pretOpt.isEmpty() || !pretOpt.get().getAdherent().equals(adherent)) return "redirect:/adherent/mes-prets";
        prolongementPret.setPretLivre(pretOpt.get());
        prolongementPret.setEtatProlongation("en_attente");
        prolongementPretService.saveProlongement(prolongementPret);
        redirectAttributes.addFlashAttribute("success", "Demande de prolongement envoyée");
        return "redirect:/adherent/mes-prolongements";
    }

    @GetMapping("/adherent/catalogue")
    public String catalogue(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) return "redirect:/login";
        String username = userDetails.getUsername();
        var userOpt = userService.getUserByUsername(username);
        if (userOpt.isEmpty()) return "redirect:/login";
        var adherentOpt = adherentService.getAdherentByUser(userOpt.get());
        if (adherentOpt.isEmpty()) return "redirect:/login";
        Adherent adherent = adherentOpt.get();
        
        List<Livre> livres = livreService.getAllLivresWithExemplaires();
        int notificationsNonLuesCount = notificationService.getNotificationsNonLuesByAdherent(adherent).size();
        
        model.addAttribute("livres", livres);
        model.addAttribute("adherent", adherent);
        model.addAttribute("notificationsNonLuesCount", notificationsNonLuesCount);
        return "adherent/catalogue";
    }

    @GetMapping("/adherent/livre/{id}")
    public String detailLivre(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) return "redirect:/login";
        String username = userDetails.getUsername();
        var userOpt = userService.getUserByUsername(username);
        if (userOpt.isEmpty()) return "redirect:/login";
        var adherentOpt = adherentService.getAdherentByUser(userOpt.get());
        if (adherentOpt.isEmpty()) return "redirect:/login";
        Adherent adherent = adherentOpt.get();
        
        Optional<Livre> livreOpt = livreService.getLivreById(id);
        if (livreOpt.isEmpty()) return "redirect:/adherent/catalogue";
        
        Livre livre = livreOpt.get();
        List<Exemplaire> exemplairesDisponibles = exemplaireService.getExemplairesDisponiblesByLivre(livre);
        List<TypePret> typesPret = typePretService.getAllTypePrets();
        int notificationsNonLuesCount = notificationService.getNotificationsNonLuesByAdherent(adherent).size();
        
        // Vérifier si l'adhérent est pénalisé
        boolean isPenalise = adherentService.isPenalise(adherent.getIdAdherent());
        adherent.setPenalise(isPenalise);
        
        model.addAttribute("livre", livre);
        model.addAttribute("exemplairesDisponibles", exemplairesDisponibles);
        model.addAttribute("typesPret", typesPret);
        model.addAttribute("adherent", adherent);
        model.addAttribute("notificationsNonLuesCount", notificationsNonLuesCount);
        return "adherent/detail-livre";
    }

    @PostMapping("/adherent/emprunter/{exemplaireId}")
    public String emprunterLivre(@PathVariable Long exemplaireId, 
                                @RequestParam("typePretId") Long typePretId,
                                @AuthenticationPrincipal UserDetails userDetails, 
                                RedirectAttributes redirectAttributes) {
        if (userDetails == null) return "redirect:/login";
        String username = userDetails.getUsername();
        var userOpt = userService.getUserByUsername(username);
        if (userOpt.isEmpty()) return "redirect:/login";
        var adherentOpt = adherentService.getAdherentByUser(userOpt.get());
        if (adherentOpt.isEmpty()) return "redirect:/login";
        Adherent adherent = adherentOpt.get();
        
        Optional<Exemplaire> exemplaireOpt = exemplaireService.getExemplaireById(exemplaireId);
        Optional<TypePret> typePretOpt = typePretService.getTypePretById(typePretId);
        
        if (exemplaireOpt.isEmpty() || typePretOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Exemplaire ou type de prêt non trouvé");
            return "redirect:/adherent/catalogue";
        }
        
        Exemplaire exemplaire = exemplaireOpt.get();
        TypePret typePret = typePretOpt.get();
        
        // Vérifier si l'exemplaire est disponible
        if (!exemplaire.estDisponible()) {
            redirectAttributes.addFlashAttribute("error", "Cet exemplaire n'est pas disponible");
            return "redirect:/adherent/livre/" + exemplaire.getLivre().getIdLivre();
        }
        
        // Vérifier les limites de prêt
        if (typePret.getNomTypePret().equalsIgnoreCase("domicile")) {
            if (adherent.getLivresEmpruntesDomicile() >= adherent.getMaxLivresDomicile()) {
                redirectAttributes.addFlashAttribute("error", "Vous avez atteint votre limite de prêts à domicile");
                return "redirect:/adherent/livre/" + exemplaire.getLivre().getIdLivre();
            }
        } else if (typePret.getNomTypePret().equalsIgnoreCase("sur_place")) {
            if (adherent.getLivresEmpruntesSurplace() >= adherent.getMaxLivresSurplace()) {
                redirectAttributes.addFlashAttribute("error", "Vous avez atteint votre limite de prêts sur place");
                return "redirect:/adherent/livre/" + exemplaire.getLivre().getIdLivre();
            }
        }
        
        // Créer le prêt
        PretLivre pret = new PretLivre();
        pret.setAdherent(adherent);
        pret.setExemplaire(exemplaire);
        pret.setTypePret(typePret);
        pret.setDateDebut(LocalDate.now());
        pret.setDateFin(LocalDate.now().plusDays(adherent.getDureePret()));
        pret.setEtatPret("actif");
        
        try {
            pretLivreService.savePret(pret);
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", "Vous êtes actuellement suspendu et ne pouvez pas emprunter de livres. Veuillez contacter la bibliothèque pour plus d'informations.");
            return "redirect:/adherent/livre/" + exemplaire.getLivre().getIdLivre();
        }
        
        // Mettre à jour l'état de l'exemplaire
        exemplaire.setEtat("en_pret");
        exemplaireService.saveExemplaire(exemplaire);
        
        // Mettre à jour les compteurs de l'adhérent
        if (typePret.getNomTypePret().equalsIgnoreCase("domicile")) {
            adherent.setLivresEmpruntesDomicile(adherent.getLivresEmpruntesDomicile() + 1);
        } else if (typePret.getNomTypePret().equalsIgnoreCase("sur_place")) {
            adherent.setLivresEmpruntesSurplace(adherent.getLivresEmpruntesSurplace() + 1);
        }
        adherentService.saveAdherent(adherent);
        
        redirectAttributes.addFlashAttribute("success", "Livre emprunté avec succès ! Date de retour : " + pret.getDateFin());
        return "redirect:/adherent/mes-prets";
    }

    @PostMapping("/adherent/reserver/{livreId}")
    public String reserverLivre(@PathVariable Long livreId,
                               @RequestParam("datePret") String datePretStr,
                               @RequestParam("dateFinPret") String dateFinPretStr,
                               @AuthenticationPrincipal UserDetails userDetails,
                               RedirectAttributes redirectAttributes) {
        if (userDetails == null) return "redirect:/login";
        String username = userDetails.getUsername();
        var userOpt = userService.getUserByUsername(username);
        if (userOpt.isEmpty()) return "redirect:/login";
        var adherentOpt = adherentService.getAdherentByUser(userOpt.get());
        if (adherentOpt.isEmpty()) return "redirect:/login";
        Adherent adherent = adherentOpt.get();
        
        Optional<Livre> livreOpt = livreService.getLivreById(livreId);
        if (livreOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Livre non trouvé");
            return "redirect:/adherent/catalogue";
        }
        
        Livre livre = livreOpt.get();
        
        // Vérifier si l'adhérent a déjà une réservation pour ce livre
        List<Reservation> reservationsExistantes = reservationService.getReservationsByAdherentAndLivre(adherent, livre);
        if (!reservationsExistantes.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Vous avez déjà une réservation pour ce livre");
            return "redirect:/adherent/livre/" + livreId;
        }
        
        // Créer la réservation
        Reservation reservation = new Reservation();
        reservation.setAdherent(adherent);
        reservation.setLivre(livre);
        reservation.setDatePret(LocalDate.parse(datePretStr));
        reservation.setDateFinPret(LocalDate.parse(dateFinPretStr));
        reservation.setEtatReservation("en_attente");
        
        try {
            reservationService.saveReservation(reservation);
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", "Vous êtes actuellement suspendu et ne pouvez pas faire de réservations. Veuillez contacter la bibliothèque pour plus d'informations.");
            return "redirect:/adherent/livre/" + livreId;
        }
        
        redirectAttributes.addFlashAttribute("success", "Réservation effectuée avec succès !");
        return "redirect:/adherent/mes-reservations";
    }

    @GetMapping("/adherent/mes-reservations")
    public String mesReservations(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) return "redirect:/login";
        String username = userDetails.getUsername();
        var userOpt = userService.getUserByUsername(username);
        if (userOpt.isEmpty()) return "redirect:/login";
        var adherentOpt = adherentService.getAdherentByUser(userOpt.get());
        if (adherentOpt.isEmpty()) return "redirect:/login";
        Adherent adherent = adherentOpt.get();
        
        List<Reservation> reservations = reservationService.getReservationsByAdherent(adherent);
        int notificationsNonLuesCount = notificationService.getNotificationsNonLuesByAdherent(adherent).size();
        
        model.addAttribute("reservations", reservations);
        model.addAttribute("adherent", adherent);
        model.addAttribute("notificationsNonLuesCount", notificationsNonLuesCount);
        return "adherent/mes-reservations";
    }

    @PostMapping("/adherent/annuler-reservation/{reservationId}")
    public String annulerReservation(@PathVariable Long reservationId,
                                    @AuthenticationPrincipal UserDetails userDetails,
                                    RedirectAttributes redirectAttributes) {
        if (userDetails == null) return "redirect:/login";
        String username = userDetails.getUsername();
        var userOpt = userService.getUserByUsername(username);
        if (userOpt.isEmpty()) return "redirect:/login";
        var adherentOpt = adherentService.getAdherentByUser(userOpt.get());
        if (adherentOpt.isEmpty()) return "redirect:/login";
        Adherent adherent = adherentOpt.get();
        
        Optional<Reservation> reservationOpt = reservationService.getReservationById(reservationId);
        if (reservationOpt.isEmpty() || !reservationOpt.get().getAdherent().equals(adherent)) {
            redirectAttributes.addFlashAttribute("error", "Réservation non trouvée");
            return "redirect:/adherent/mes-reservations";
        }
        
        reservationService.deleteReservation(reservationId);
        redirectAttributes.addFlashAttribute("success", "Réservation annulée avec succès");
        return "redirect:/adherent/mes-reservations";
    }

    @GetMapping("/adherent/mes-prets")
    public String mesPretsAdherent(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        return mesPrets(userDetails, model);
    }

    @GetMapping("/adherent/mes-prolongements")
    public String mesProlongementsAdherent(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        return mesProlongements(userDetails, model);
    }
} 