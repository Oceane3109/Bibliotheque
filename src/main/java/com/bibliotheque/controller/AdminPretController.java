package com.bibliotheque.controller;

import com.bibliotheque.model.PretLivre;
import com.bibliotheque.service.PretLivreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin/prets")
@PreAuthorize("hasRole('ADMIN')")
public class AdminPretController {

    private final PretLivreService pretLivreService;

    @Autowired
    public AdminPretController(PretLivreService pretLivreService) {
        this.pretLivreService = pretLivreService;
    }

    @GetMapping("/list")
    public String listPrets(Model model) {
        List<PretLivre> prets = pretLivreService.getAllPrets();
        model.addAttribute("prets", prets);
        return "admin/prets/list";
    }
} 