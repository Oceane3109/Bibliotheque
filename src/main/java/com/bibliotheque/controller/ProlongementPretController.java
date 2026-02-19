package com.bibliotheque.controller;

import com.bibliotheque.model.ProlongementPret;
import com.bibliotheque.model.PretLivre;
import com.bibliotheque.model.Adherent;
import com.bibliotheque.service.ProlongementPretService;
import com.bibliotheque.service.PretLivreService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/prolongements")
@PreAuthorize("hasRole('ADMIN')")
public class ProlongementPretController {
    private final ProlongementPretService prolongementPretService;
    private final PretLivreService pretLivreService;

    @Autowired
    public ProlongementPretController(ProlongementPretService prolongementPretService, PretLivreService pretLivreService) {
        this.prolongementPretService = prolongementPretService;
        this.pretLivreService = pretLivreService;
    }

    @GetMapping
    public String listProlongements(Model model) {
        List<ProlongementPret> prolongements = prolongementPretService.getAllProlongementsWithRelations();
        model.addAttribute("prolongements", prolongements);
        return "admin/prolongements/list";
    }

    @PostMapping("/delete/{id}")
    public String deleteProlongement(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        prolongementPretService.deleteProlongement(id);
        redirectAttributes.addFlashAttribute("success", "Prolongement supprimé avec succès");
        return "redirect:/admin/prolongements";
    }

    @PostMapping("/valider/{id}")
    public String validerProlongement(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<ProlongementPret> prolongementOpt = prolongementPretService.getProlongementById(id);
        if (prolongementOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Prolongement non trouvé");
            return "redirect:/admin/prolongements";
        }
        ProlongementPret prolongement = prolongementOpt.get();
        if (!"en_attente".equals(prolongement.getEtatProlongation())) {
            redirectAttributes.addFlashAttribute("error", "Cette demande a déjà été traitée");
            return "redirect:/admin/prolongements";
        }
        // Vérifier le quota de l'adhérent
        Adherent adherent = prolongement.getPretLivre().getAdherent();
        int prolongementsApprouves = prolongementPretService.getNombreProlongementsApprouvesByAdherent(adherent);
        if (prolongementsApprouves >= adherent.getQuotaProlongements()) {
            redirectAttributes.addFlashAttribute("error", "L'adhérent a utilisé tout son quota de prolongements");
            return "redirect:/admin/prolongements";
        }
        // Marquer le prolongement comme approuvé
        prolongement.setEtatProlongation("approuvee");
        prolongementPretService.saveProlongement(prolongement);
        // Mettre à jour la date de fin du prêt existant
        PretLivre pret = prolongement.getPretLivre();
        pret.setDateFin(prolongement.getNouvelleDateFin());
        pret.setEtatPret("en_cours"); // Toujours en cours après prolongement
        pretLivreService.savePret(pret);
        redirectAttributes.addFlashAttribute("success", "Prolongement approuvé et date de retour mise à jour");
        return "redirect:/admin/prolongements";
    }

    @PostMapping("/refuser/{id}")
    public String refuserProlongement(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<ProlongementPret> prolongementOpt = prolongementPretService.getProlongementById(id);
        if (prolongementOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Prolongement non trouvé");
            return "redirect:/admin/prolongements";
        }
        
        ProlongementPret prolongement = prolongementOpt.get();
        if (!"en_attente".equals(prolongement.getEtatProlongation())) {
            redirectAttributes.addFlashAttribute("error", "Cette demande a déjà été traitée");
            return "redirect:/admin/prolongements";
        }
        
        // Marquer le prolongement comme refusé
        prolongement.setEtatProlongation("refusee");
        prolongementPretService.saveProlongement(prolongement);
        
        redirectAttributes.addFlashAttribute("success", "Prolongement refusé");
        return "redirect:/admin/prolongements";
    }
} 