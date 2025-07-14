package com.bibliotheque.controller;

import com.bibliotheque.model.PretLivre;
import com.bibliotheque.service.PretLivreService;
import com.bibliotheque.service.AdherentService;
import com.bibliotheque.service.ExemplaireService;
import com.bibliotheque.service.TypePretService;
import com.bibliotheque.model.Adherent;
import com.bibliotheque.model.Exemplaire;
import com.bibliotheque.model.TypePret;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin/prets")
@PreAuthorize("hasRole('ADMIN')")
public class AdminPretController {

    private final PretLivreService pretLivreService;
    private final AdherentService adherentService;
    private final ExemplaireService exemplaireService;
    private final TypePretService typePretService;

    @Autowired
    public AdminPretController(PretLivreService pretLivreService, AdherentService adherentService, ExemplaireService exemplaireService, TypePretService typePretService) {
        this.pretLivreService = pretLivreService;
        this.adherentService = adherentService;
        this.exemplaireService = exemplaireService;
        this.typePretService = typePretService;
    }

    @GetMapping("/list")
    public String listPrets(Model model) {
        List<PretLivre> pretsActifs = pretLivreService.getAllPretsActifs();
        List<PretLivre> pretsInactifs = pretLivreService.getAllPretsInactifs();
        model.addAttribute("pretsActifs", pretsActifs);
        model.addAttribute("pretsInactifs", pretsInactifs);
        return "admin/prets/list";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        var pretOpt = pretLivreService.getPretById(id);
        if (pretOpt.isEmpty()) {
            return "redirect:/admin/prets/list";
        }
        model.addAttribute("pret", pretOpt.get());
        model.addAttribute("adherents", adherentService.getAllAdherents());
        model.addAttribute("exemplaires", exemplaireService.getAllExemplaires());
        model.addAttribute("typesPret", typePretService.getAllTypePrets());
        return "admin/prets/form";
    }

    @PostMapping("/edit/{id}")
    public String updatePret(@PathVariable Long id,
                            @ModelAttribute("pret") PretLivre pret,
                            BindingResult result,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("adherents", adherentService.getAllAdherents());
            model.addAttribute("exemplaires", exemplaireService.getAllExemplaires());
            model.addAttribute("typesPret", typePretService.getAllTypePrets());
            return "admin/prets/form";
        }
        pret.setIdPret(id);
        try {
            pretLivreService.savePret(pret);
            redirectAttributes.addFlashAttribute("success", "Prêt modifié avec succès");
            return "redirect:/admin/prets/list";
        } catch (IllegalStateException e) {
            model.addAttribute("adherents", adherentService.getAllAdherents());
            model.addAttribute("exemplaires", exemplaireService.getAllExemplaires());
            model.addAttribute("typesPret", typePretService.getAllTypePrets());
            model.addAttribute("error", e.getMessage());
            return "admin/prets/form";
        }
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("pret", new PretLivre());
        model.addAttribute("adherents", adherentService.getAllAdherents());
        model.addAttribute("exemplaires", exemplaireService.getAllExemplaires());
        model.addAttribute("typesPret", typePretService.getAllTypePrets());
        return "admin/prets/form";
    }

    @PostMapping("/create")
    public String createPret(@ModelAttribute("pret") PretLivre pret,
                            BindingResult result,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("adherents", adherentService.getAllAdherents());
            model.addAttribute("exemplaires", exemplaireService.getAllExemplaires());
            model.addAttribute("typesPret", typePretService.getAllTypePrets());
            return "admin/prets/form";
        }
        try {
            if (pret.getDatePret() == null) {
                pret.setDatePret(LocalDate.now());
            }
            pretLivreService.savePret(pret);
            redirectAttributes.addFlashAttribute("success", "Prêt créé avec succès");
            return "redirect:/admin/prets/list";
        } catch (IllegalStateException e) {
            model.addAttribute("adherents", adherentService.getAllAdherents());
            model.addAttribute("exemplaires", exemplaireService.getAllExemplaires());
            model.addAttribute("typesPret", typePretService.getAllTypePrets());
            model.addAttribute("error", e.getMessage());
            return "admin/prets/form";
        }
    }

    @GetMapping("")
    public String redirectToList() {
        return "redirect:/admin/prets/list";
    }

    @RequestMapping(value = "/retourner/{id}", method = {RequestMethod.GET, RequestMethod.POST})
    public String marquerCommeRetourne(@PathVariable Long id, 
                                       @RequestParam(value = "dateRetourEffective", required = false) String dateRetourEffectiveStr,
                                       RedirectAttributes redirectAttributes) {
        try {
            java.time.LocalDate dateRetourEffective = null;
            if (dateRetourEffectiveStr != null && !dateRetourEffectiveStr.isEmpty()) {
                dateRetourEffective = java.time.LocalDate.parse(dateRetourEffectiveStr);
            }
            pretLivreService.marquerPretCommeRetourne(id, dateRetourEffective);
            redirectAttributes.addFlashAttribute("success", "Livre marqué comme retourné avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors du retour du livre: " + e.getMessage());
        }
        return "redirect:/admin/prets/list";
    }

    @GetMapping("/adherent/{id}")
    public String listPretsByAdherent(@PathVariable Long id, Model model) {
        var adherentOpt = adherentService.getAdherentById(id);
        if (adherentOpt.isEmpty()) {
            return "redirect:/admin/adherents/list";
        }
        Adherent adherent = adherentOpt.get();
        List<PretLivre> prets = pretLivreService.getPretsByAdherent(adherent);
        model.addAttribute("adherent", adherent);
        model.addAttribute("prets", prets);
        return "admin/prets/list-by-adherent";
    }
} 