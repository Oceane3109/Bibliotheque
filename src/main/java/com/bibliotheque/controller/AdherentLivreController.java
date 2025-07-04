package com.bibliotheque.controller;

import com.bibliotheque.model.Adherent;
import com.bibliotheque.model.Livre;
import com.bibliotheque.model.NoteLivre;
import com.bibliotheque.service.AdherentService;
import com.bibliotheque.service.LivreService;
import com.bibliotheque.service.NoteLivreService;
import com.bibliotheque.service.UserService;
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

    @Autowired
    public AdherentLivreController(UserService userService, AdherentService adherentService, LivreService livreService, NoteLivreService noteLivreService) {
        this.userService = userService;
        this.adherentService = adherentService;
        this.livreService = livreService;
        this.noteLivreService = noteLivreService;
    }

    @GetMapping("/adherent/livre/{id}")
    public String detailLivreAdherent(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails, Model model) {
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
        // Notation
        Double moyenneNote = noteLivreService.getAverageNoteByLivre(livre);
        List<NoteLivre> avis = noteLivreService.getNotesByLivre(livre);
        boolean noteDejaDonnee = noteLivreService.getNoteByLivreAndAdherent(livre, adherent).isPresent();
        model.addAttribute("livre", livre);
        model.addAttribute("moyenneNote", moyenneNote);
        model.addAttribute("avis", avis);
        model.addAttribute("noteDejaDonnee", noteDejaDonnee);
        return "adherent/detail-livre";
    }
} 