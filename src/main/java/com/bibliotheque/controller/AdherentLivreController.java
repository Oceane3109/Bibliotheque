package com.bibliotheque.controller;

import com.bibliotheque.model.Adherent;
import com.bibliotheque.model.Livre;
import com.bibliotheque.model.NoteLivre;
import com.bibliotheque.service.AdherentService;
import com.bibliotheque.service.LivreService;
import com.bibliotheque.service.NoteLivreService;
import com.bibliotheque.service.TypePretService;
import com.bibliotheque.service.UserService;
import com.bibliotheque.service.JourFerieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@Controller
public class AdherentLivreController {
    private final UserService userService;
    private final AdherentService adherentService;
    private final LivreService livreService;
    private final NoteLivreService noteLivreService;
    private final TypePretService typePretService;
    private final JourFerieService jourFerieService;

    @Autowired
    public AdherentLivreController(UserService userService, AdherentService adherentService, LivreService livreService, NoteLivreService noteLivreService, TypePretService typePretService, JourFerieService jourFerieService) {
        this.userService = userService;
        this.adherentService = adherentService;
        this.livreService = livreService;
        this.noteLivreService = noteLivreService;
        this.typePretService = typePretService;
        this.jourFerieService = jourFerieService;
    }

    @GetMapping("/adherent/livre/{id}")
    public String detailLivreAdherent(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) return "redirect:/login";
        String username = userDetails.getUsername();
        var adherentOpt = adherentService.getAdherentByUserUsernameWithPrets(username);
        if (adherentOpt.isEmpty()) return "redirect:/login";
        Adherent adherent = adherentOpt.get();
        Optional<Livre> livreOpt = livreService.getLivreById(id);
        if (livreOpt.isEmpty()) return "redirect:/adherent/catalogue";
        Livre livre = livreOpt.get();
        // Notation
        Double moyenneNote = noteLivreService.getAverageNoteByLivre(livre);
        List<NoteLivre> avis = noteLivreService.getNotesByLivre(livre);
        boolean noteDejaDonnee = noteLivreService.getNoteByLivreAndAdherent(livre, adherent).isPresent();

        // Calcul dynamique des prêts en cours
        long nbDomicileActifs = adherent.getPrets().stream()
            .filter(pret -> "en_cours".equals(pret.getEtatPret()))
            .filter(pret -> pret.getTypePret() != null && pret.getTypePret().getNomTypePret().equalsIgnoreCase("domicile"))
            .count();
        long nbSurPlaceActifs = adherent.getPrets().stream()
            .filter(pret -> "en_cours".equals(pret.getEtatPret()))
            .filter(pret -> pret.getTypePret() != null && (pret.getTypePret().getNomTypePret().equalsIgnoreCase("sur_place") || pret.getTypePret().getNomTypePret().equalsIgnoreCase("sur place")))
            .count();
        model.addAttribute("nbDomicileActifs", nbDomicileActifs);
        model.addAttribute("nbSurPlaceActifs", nbSurPlaceActifs);

        model.addAttribute("livre", livre);
        model.addAttribute("moyenneNote", moyenneNote);
        model.addAttribute("avis", avis);
        model.addAttribute("noteDejaDonnee", noteDejaDonnee);
        model.addAttribute("adherent", adherent);
        model.addAttribute("typesPret", typePretService.getAllTypePrets());
        // Ajout des jours fériés au format ISO yyyy-MM-dd pour le JS
        List<String> joursFeriesIso = jourFerieService.getAllJoursFeries().stream()
            .map(jf -> jf.getDateFeriee().toString())
            .toList();
        model.addAttribute("joursFeriesIso", joursFeriesIso);
        return "adherent/detail-livre";
    }
} 