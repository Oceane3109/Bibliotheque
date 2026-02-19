package com.bibliotheque.controller;

import com.bibliotheque.model.Notification;
import com.bibliotheque.service.NotificationService;
import com.bibliotheque.service.AdherentService;
import com.bibliotheque.model.Adherent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/notifications")
@PreAuthorize("hasRole('ADMIN')")
public class AdminNotificationController {

    private final NotificationService notificationService;
    private final AdherentService adherentService;

    @Autowired
    public AdminNotificationController(NotificationService notificationService, AdherentService adherentService) {
        this.notificationService = notificationService;
        this.adherentService = adherentService;
    }

    @GetMapping("/list")
    public String listNotifications(Model model) {
        List<Notification> notifications = notificationService.getAllNotifications();
        model.addAttribute("notifications", notifications);
        return "admin/notifications/list";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        var notifOpt = notificationService.getNotificationById(id);
        if (notifOpt.isEmpty()) {
            return "redirect:/admin/notifications/list";
        }
        model.addAttribute("notification", notifOpt.get());
        model.addAttribute("adherents", adherentService.getAllAdherents());
        return "admin/notifications/form";
    }

    @PostMapping("/edit/{id}")
    public String updateNotification(@PathVariable Long id,
                                     @ModelAttribute("notification") Notification notification,
                                     BindingResult result,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("adherents", adherentService.getAllAdherents());
            return "admin/notifications/form";
        }
        notification.setIdNotification(id);
        notificationService.saveNotification(notification);
        redirectAttributes.addFlashAttribute("success", "Notification modifiée avec succès");
        return "redirect:/admin/notifications/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("notification", new Notification());
        model.addAttribute("adherents", adherentService.getAllAdherents());
        return "admin/notifications/form";
    }

    @PostMapping("/create")
    public String createNotification(@ModelAttribute("notification") Notification notification,
                                     BindingResult result,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("adherents", adherentService.getAllAdherents());
            return "admin/notifications/form";
        }
        notificationService.saveNotification(notification);
        redirectAttributes.addFlashAttribute("success", "Notification créée avec succès");
        return "redirect:/admin/notifications/list";
    }
} 