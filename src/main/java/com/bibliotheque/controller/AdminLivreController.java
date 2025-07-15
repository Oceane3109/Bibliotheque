package com.bibliotheque.controller;

import com.bibliotheque.model.Livre;
import com.bibliotheque.service.LivreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import com.bibliotheque.model.Exemplaire;

@Controller
@RequestMapping("/admin/livres")
@PreAuthorize("hasRole('ADMIN')")
public class AdminLivreController {

    private final LivreService livreService;

    @Autowired
    public AdminLivreController(LivreService livreService) {
        this.livreService = livreService;
    }

    @GetMapping("/list")
    public String listLivres(Model model) {
        List<Livre> livres = livreService.getAllLivresWithExemplaires();
        Map<Long, Long> exemplairesDisponibles = new HashMap<>();
        for (Livre livre : livres) {
            long nbDispo = livre.getExemplaires().stream()
                .filter(Exemplaire::estDisponible)
                .count();
            exemplairesDisponibles.put(livre.getIdLivre(), nbDispo);
        }
        model.addAttribute("livres", livres);
        model.addAttribute("exemplairesDisponibles", exemplairesDisponibles);
        return "admin/livres/list";
    }

    @GetMapping("/{id}")
    public String detailLivreAdmin(@PathVariable Long id, Model model) {
        Livre livre = livreService.getLivreWithExemplairesById(id).orElseThrow();
        model.addAttribute("livre", livre);
        return "admin/livres/detail";
    }

    @GetMapping("/{id}/json")
    @ResponseBody
    public Livre detailLivreAdminJson(@PathVariable Long id) {
        return livreService.getLivreWithExemplairesById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Livre non trouvé"));
    }
} 