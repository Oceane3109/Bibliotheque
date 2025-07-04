package com.bibliotheque.controller;

import com.bibliotheque.model.Adherent;
import com.bibliotheque.service.AdherentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin/adherents")
@PreAuthorize("hasRole('ADMIN')")
public class AdminAdherentController {

    private final AdherentService adherentService;

    @Autowired
    public AdminAdherentController(AdherentService adherentService) {
        this.adherentService = adherentService;
    }

    @GetMapping("/list")
    public String listAdherents(Model model) {
        List<Adherent> adherents = adherentService.getAllAdherents();
        model.addAttribute("adherents", adherents);
        return "admin/adherents/list";
    }
} 