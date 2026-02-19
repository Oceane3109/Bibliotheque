package com.bibliotheque.controller;

import com.bibliotheque.service.LivreService;
import com.bibliotheque.service.AdherentService;
import com.bibliotheque.service.PretLivreService;
import com.bibliotheque.service.ReservationService;
import com.bibliotheque.service.PenaliteService;
import com.bibliotheque.service.NotificationService;
import com.bibliotheque.service.NoteLivreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminDashboardController {
    private final LivreService livreService;
    private final AdherentService adherentService;
    private final PretLivreService pretLivreService;
    private final ReservationService reservationService;
    private final PenaliteService penaliteService;
    private final NotificationService notificationService;
    private final NoteLivreService noteLivreService;

    @Autowired
    public AdminDashboardController(LivreService livreService,
                                   AdherentService adherentService,
                                   PretLivreService pretLivreService,
                                   ReservationService reservationService,
                                   PenaliteService penaliteService,
                                   NotificationService notificationService,
                                   NoteLivreService noteLivreService) {
        this.livreService = livreService;
        this.adherentService = adherentService;
        this.pretLivreService = pretLivreService;
        this.reservationService = reservationService;
        this.penaliteService = penaliteService;
        this.notificationService = notificationService;
        this.noteLivreService = noteLivreService;
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {
        model.addAttribute("totalLivres", livreService.getAllLivres().size());
        model.addAttribute("totalAdherents", adherentService.getAllAdherents().size());
        model.addAttribute("totalPretsActifs", pretLivreService.getPretsByEtat("en_cours").size());
        model.addAttribute("totalReservations", reservationService.getAllReservations().size());
        model.addAttribute("totalPenalites", penaliteService.getAllPenalites().size());
        model.addAttribute("totalNotifications", notificationService.getAllNotifications().size());
        // Statistiques de notes
        model.addAttribute("totalAvis", noteLivreService.countAllNotes());
        model.addAttribute("nbNotes5", noteLivreService.countByNote(5));
        model.addAttribute("nbNotes4", noteLivreService.countByNote(4));
        model.addAttribute("nbNotes3", noteLivreService.countByNote(3));
        model.addAttribute("nbNotes2", noteLivreService.countByNote(2));
        model.addAttribute("nbNotes1", noteLivreService.countByNote(1));
        // Top livres
        model.addAttribute("topLivres", noteLivreService.getTopLivresByNote());
        model.addAttribute("lastAvis", noteLivreService.getLastAvisWithDetails());
        return "admin/dashboard";
    }
} 