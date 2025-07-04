package com.bibliotheque.controller;

import com.bibliotheque.model.NoteLivre;
import com.bibliotheque.model.Livre;
import com.bibliotheque.model.Adherent;
import com.bibliotheque.service.NoteLivreService;
import com.bibliotheque.service.LivreService;
import com.bibliotheque.service.AdherentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequestMapping("/adherent/livre")
public class NoteLivreController {
    private final NoteLivreService noteLivreService;
    private final LivreService livreService;
    private final AdherentService adherentService;

    @Autowired
    public NoteLivreController(NoteLivreService noteLivreService, LivreService livreService, AdherentService adherentService) {
        this.noteLivreService = noteLivreService;
        this.livreService = livreService;
        this.adherentService = adherentService;
    }

    @PostMapping("/{livreId}/noter")
    public String noterLivre(@PathVariable Long livreId,
                             @RequestParam int note,
                             @RequestParam(required = false) String commentaire,
                             @AuthenticationPrincipal UserDetails userDetails,
                             RedirectAttributes redirectAttributes) {
        if (userDetails == null) return "redirect:/login";
        String username = userDetails.getUsername();
        Optional<Adherent> adherentOpt = adherentService.getAdherentByUserUsername(username);
        if (adherentOpt.isEmpty()) return "redirect:/login";
        Optional<Livre> livreOpt = livreService.getLivreById(livreId);
        if (livreOpt.isEmpty()) return "redirect:/adherent/catalogue";
        Adherent adherent = adherentOpt.get();
        Livre livre = livreOpt.get();
        // Un adhérent ne peut noter qu'une fois
        Optional<NoteLivre> existing = noteLivreService.getNoteByLivreAndAdherent(livre, adherent);
        if (existing.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Vous avez déjà noté ce livre.");
            return "redirect:/adherent/livre/" + livreId;
        }
        NoteLivre noteLivre = new NoteLivre();
        noteLivre.setLivre(livre);
        noteLivre.setAdherent(adherent);
        noteLivre.setNote(note);
        noteLivre.setCommentaire(commentaire);
        noteLivre.setDate(LocalDateTime.now());
        noteLivreService.saveNote(noteLivre);
        redirectAttributes.addFlashAttribute("success", "Merci pour votre avis !");
        return "redirect:/adherent/livre/" + livreId;
    }
} 