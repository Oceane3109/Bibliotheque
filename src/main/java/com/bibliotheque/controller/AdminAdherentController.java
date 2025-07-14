package com.bibliotheque.controller;

import com.bibliotheque.model.Adherent;
import com.bibliotheque.model.Abonnement;
import com.bibliotheque.service.AdherentService;
import com.bibliotheque.service.AbonnementService;
import com.bibliotheque.service.TypeAdherentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/admin/adherents")
@PreAuthorize("hasRole('ADMIN')")
public class AdminAdherentController {

    private static final Logger logger = LoggerFactory.getLogger(AdminAdherentController.class);
    private final AdherentService adherentService;
    private final TypeAdherentService typeAdherentService;
    private final AbonnementService abonnementService;

    @Autowired
    public AdminAdherentController(AdherentService adherentService, TypeAdherentService typeAdherentService, AbonnementService abonnementService) {
        this.adherentService = adherentService;
        this.typeAdherentService = typeAdherentService;
        this.abonnementService = abonnementService;
    }

    @GetMapping("/list")
    public String listAdherents(Model model) {
        List<Adherent> adherents = adherentService.getAllAdherents();
        // Pour chaque adhérent, déterminer s’il a un abonnement en attente ou actif
        List<Adherent> adherentsWithAbonnement = adherents.stream().peek(adherent -> {
            boolean enAttente = abonnementService.getAbonnementsByAdherent(adherent).stream()
                .anyMatch(a -> "en_attente".equals(a.getStatut()));
            boolean actif = abonnementService.getAbonnementActifByAdherent(adherent).isPresent();
            adherent.getClass().getDeclaredFields(); // Pour éviter l’optimisation du stream
            adherent.getClass(); // idem
            // Utilisation de set/get génériques via attributs dynamiques (ou Map) si le modèle ne prévoit pas ces champs
            // Ici, on va utiliser model.addAttribute pour transmettre une Map<id, bool>
        }).collect(Collectors.toList());
        // Préparer des maps pour l’affichage des badges
        java.util.Map<Long, Boolean> abonnementEnAttenteMap = new java.util.HashMap<>();
        java.util.Map<Long, Boolean> abonnementActifMap = new java.util.HashMap<>();
        for (Adherent adherent : adherents) {
            boolean enAttente = abonnementService.getAbonnementsByAdherent(adherent).stream()
                .anyMatch(a -> "en_attente".equals(a.getStatut()));
            boolean actif = abonnementService.getAbonnementActifByAdherent(adherent).isPresent();
            abonnementEnAttenteMap.put(adherent.getIdAdherent(), enAttente);
            abonnementActifMap.put(adherent.getIdAdherent(), actif);
        }
        model.addAttribute("adherents", adherents);
        model.addAttribute("abonnementEnAttenteMap", abonnementEnAttenteMap);
        model.addAttribute("abonnementActifMap", abonnementActifMap);
        return "admin/adherents/list";
    }

    @GetMapping("/edit/{id}")
    public String editAdherent(@PathVariable Long id, Model model) {
        Optional<Adherent> adherentOpt = adherentService.getAdherentById(id);
        if (adherentOpt.isPresent()) {
            Adherent adherent = adherentOpt.get();
            logger.info("Affichage fiche adhérent id={}, nom={}, adresse={}, email={}, téléphone={}",
                adherent.getIdAdherent(), adherent.getNom(), adherent.getAdresse(), adherent.getEmail(), adherent.getTelephone());
            // Chercher l’abonnement actif ou en attente
            Abonnement abonnement = abonnementService.getAbonnementActifByAdherent(adherent)
                .orElseGet(() -> abonnementService.getAbonnementsByAdherent(adherent).stream()
                    .filter(a -> "en_attente".equals(a.getStatut()))
                    .findFirst().orElse(null));
            model.addAttribute("adherent", adherent);
            model.addAttribute("abonnement", abonnement);
            model.addAttribute("typeAdherents", typeAdherentService.getAllTypeAdherents());
            return "admin/adherents/edit";
        } else {
            return "redirect:/admin/adherents/list";
        }
    }

    @PostMapping("/edit/{id}")
    public String updateAdherent(@PathVariable Long id,
                                 @org.springframework.web.bind.annotation.ModelAttribute Adherent adherent,
                                 org.springframework.validation.BindingResult result,
                                 org.springframework.ui.Model model,
                                 org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        logger.info("Tentative de modification de l'adhérent id={} : {}", id, adherent);
        if (result.hasErrors()) {
            logger.warn("Erreur de validation lors de la modification de l'adhérent id={}", id);
            model.addAttribute("adherent", adherent);
            model.addAttribute("typeAdherents", typeAdherentService.getAllTypeAdherents());
            model.addAttribute("error", "Erreur de validation. Merci de vérifier les champs obligatoires.");
            return "admin/adherents/edit";
        }
        var existingOpt = adherentService.getAdherentById(id);
        if (existingOpt.isEmpty()) {
            logger.error("Adhérent id={} introuvable pour modification", id);
            redirectAttributes.addFlashAttribute("error", "Adhérent introuvable.");
            return "redirect:/admin/adherents/list";
        }
        Adherent existing = existingOpt.get();
        adherent.setIdAdherent(id);
        adherent.setUser(existing.getUser());
        adherent.setDateInscription(existing.getDateInscription());
        if (adherent.getTypeAdherent() != null && adherent.getTypeAdherent().getIdTypeAdherent() != null) {
            typeAdherentService.getTypeAdherentById(adherent.getTypeAdherent().getIdTypeAdherent())
                .ifPresent(adherent::setTypeAdherent);
        }
        try {
            adherentService.saveAdherent(adherent);
            logger.info("Adhérent id={} modifié avec succès", id);
            redirectAttributes.addFlashAttribute("success", "Adhérent modifié avec succès");
            return "redirect:/admin/adherents/edit/" + id;
        } catch (Exception e) {
            logger.error("Erreur lors de la sauvegarde de l'adhérent id={}", id, e);
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la sauvegarde. Merci de vérifier les champs et réessayer.");
            return "redirect:/admin/adherents/edit/" + id;
        }
    }
} 