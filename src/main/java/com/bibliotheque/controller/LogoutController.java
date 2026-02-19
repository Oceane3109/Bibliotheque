package com.bibliotheque.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LogoutController {
    
    @GetMapping("/logout")
    public String logout() {
        return "redirect:/login?logout";
    }
    
    @PostMapping("/logout")
    public String logoutPost() {
        return "redirect:/login?logout";
    }
} 