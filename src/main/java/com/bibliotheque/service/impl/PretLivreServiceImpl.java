package com.bibliotheque.service.impl;

import com.bibliotheque.model.PretLivre;
import com.bibliotheque.model.Adherent;
import com.bibliotheque.model.Exemplaire;
import com.bibliotheque.model.TypePret;
import com.bibliotheque.repository.PretLivreRepository;
import com.bibliotheque.service.PretLivreService;
import com.bibliotheque.service.AdherentService;
import com.bibliotheque.service.NotificationService;
import com.bibliotheque.model.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PretLivreServiceImpl implements PretLivreService {
    private final PretLivreRepository pretLivreRepository;
    private final AdherentService adherentService;
    private final NotificationService notificationService;

    @Autowired
    public PretLivreServiceImpl(PretLivreRepository pretLivreRepository, AdherentService adherentService, NotificationService notificationService) {
        this.pretLivreRepository = pretLivreRepository;
        this.adherentService = adherentService;
        this.notificationService = notificationService;
    }

    @Override
    public PretLivre savePret(PretLivre pretLivre) {
        if (pretLivre.getAdherent() != null && adherentService.isPenalise(pretLivre.getAdherent().getIdAdherent())) {
            throw new IllegalStateException("Cet adhérent est suspendu et ne peut pas emprunter.");
        }
        boolean wasRetard = false;
        if (pretLivre.getIdPret() != null) {
            var oldPretOpt = pretLivreRepository.findById(pretLivre.getIdPret());
            if (oldPretOpt.isPresent()) {
                wasRetard = "retard".equals(oldPretOpt.get().getEtatPret());
            }
        }
        PretLivre saved = pretLivreRepository.save(pretLivre);
        if ("retard".equals(saved.getEtatPret()) && !wasRetard) {
            Notification notif = new Notification();
            notif.setAdherent(saved.getAdherent());
            notif.setTitre("Prêt en retard");
            notif.setMessage("Vous avez un prêt en retard. Merci de rapporter le livre au plus vite.");
            notif.setDateEnvoi(LocalDateTime.now());
            notif.setLu(false);
            notificationService.saveNotification(notif);
        }
        return saved;
    }

    @Override
    public Optional<PretLivre> getPretById(Long id) {
        return pretLivreRepository.findById(id);
    }

    @Override
    public List<PretLivre> getAllPrets() {
        return pretLivreRepository.findAll();
    }

    @Override
    public List<PretLivre> getPretsByAdherent(Adherent adherent) {
        return pretLivreRepository.findByAdherent(adherent);
    }

    @Override
    public List<PretLivre> getPretsByExemplaire(Exemplaire exemplaire) {
        return pretLivreRepository.findByExemplaire(exemplaire);
    }

    @Override
    public List<PretLivre> getPretsByTypePret(TypePret typePret) {
        return pretLivreRepository.findByTypePret(typePret);
    }

    @Override
    public List<PretLivre> getPretsByEtat(String etatPret) {
        return pretLivreRepository.findByEtatPret(etatPret);
    }

    @Override
    public List<PretLivre> getPretsByAdherentAndEtat(Adherent adherent, String etatPret) {
        return pretLivreRepository.findByAdherentAndEtatPret(adherent, etatPret);
    }

    @Override
    public List<PretLivre> getPretsByExemplaireAndEtat(Exemplaire exemplaire, String etatPret) {
        return pretLivreRepository.findByExemplaireAndEtatPret(exemplaire, etatPret);
    }

    @Override
    public List<PretLivre> getPretsEnRetardAvant(LocalDate date) {
        return pretLivreRepository.findByDateFinBeforeAndEtatPret(date, "retard");
    }

    @Override
    public Optional<PretLivre> getDernierPretActifPourExemplaire(Exemplaire exemplaire) {
        return pretLivreRepository.findFirstByExemplaireAndEtatPretOrderByDateDebutDesc(exemplaire, "actif");
    }

    @Override
    public void deletePret(Long id) {
        pretLivreRepository.deleteById(id);
    }
} 