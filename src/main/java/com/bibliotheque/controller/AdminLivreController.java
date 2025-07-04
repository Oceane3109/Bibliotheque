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

import java.util.List;

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
        model.addAttribute("livres", livres);
        return "admin/livres/list";
    }

    @GetMapping("/{id}")
    public String detailLivreAdmin(@PathVariable Long id, Model model) {
        Livre livre = livreService.getLivreById(id).orElseThrow();
        model.addAttribute("livre", livre);
        return "admin/livres/detail";
    }
} 