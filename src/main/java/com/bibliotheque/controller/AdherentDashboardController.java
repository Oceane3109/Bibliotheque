package com.bibliotheque.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdherentDashboardController {
    @GetMapping("/adherent/dashboard")
    public String adherentDashboard() {
        return "adherent/dashboard";
    }
} 