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
import org.springframework.scheduling.annotation.Scheduled;

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
                wasRetard = "en_retard".equals(oldPretOpt.get().getEtatPret());
            }
        }
        // Détection automatique du retard
        if (pretLivre.getDateFin() != null && pretLivre.getEtatPret().equals("en_cours") && pretLivre.getDateFin().isBefore(LocalDate.now())) {
            pretLivre.setEtatPret("en_retard");
        }
        PretLivre saved = pretLivreRepository.save(pretLivre);
        if ("en_retard".equals(saved.getEtatPret()) && !wasRetard) {
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
    public Optional<PretLivre> getPretByIdWithRelations(Long id) {
        return pretLivreRepository.findByIdWithRelations(id);
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

    @Override
    public List<PretLivre> getPretsActifsByAdherent(Adherent adherent) {
        return pretLivreRepository.findByAdherentAndEtatPret(adherent, "en_cours");
    }

    @Override
    public List<PretLivre> getPretsInactifsByAdherent(Adherent adherent) {
        return pretLivreRepository.findByAdherentAndEtatPret(adherent, "retourne");
    }

    @Override
    public List<PretLivre> getAllPretsActifs() {
        return pretLivreRepository.findByEtatPret("en_cours");
    }

    @Override
    public List<PretLivre> getAllPretsInactifs() {
        return pretLivreRepository.findByEtatPret("retourne");
    }

    @Override
    public void marquerPretCommeRetourne(Long idPret, java.time.LocalDate dateRetourEffective) {
        Optional<PretLivre> pretOpt = pretLivreRepository.findById(idPret);
        if (pretOpt.isPresent()) {
            PretLivre pret = pretOpt.get();
            System.out.println("[DEBUG] Type de prêt lors du retour : " + (pret.getTypePret() != null ? pret.getTypePret().getNomTypePret() : "null"));
            System.out.println("[DEBUG] Etat du prêt avant retour : " + pret.getEtatPret());
            pret.setEtatPret("retourne");
            pret.setActif(false); // Peut être supprimé à terme si non utilisé ailleurs
            if (dateRetourEffective != null) {
                pret.setDateRetourEffective(dateRetourEffective);
            } else {
                pret.setDateRetourEffective(LocalDate.now());
            }
            Adherent adherent = pret.getAdherent();
            if (pret.getTypePret() != null && pret.getTypePret().getNomTypePret() != null) {
                String type = pret.getTypePret().getNomTypePret().toLowerCase();
                // Suppression de la décrémentation du compteur, car le calcul est dynamique
                // if (type.contains("domicile")) {
                //     adherentService.decrementerLivresEmpruntesDomicile(adherent.getIdAdherent());
                // } else if (type.contains("surplace") || type.contains("sur place")) {
                //     adherentService.decrementerLivresEmpruntesSurPlace(adherent.getIdAdherent());
                // }
            }
            pretLivreRepository.save(pret);
        }
    }

    // Pas d'annotation @Override ici car cette méthode délègue à la version avec date
    public void marquerPretCommeRetourne(Long idPret) {
        marquerPretCommeRetourne(idPret, null);
    }

    // Notification automatique 2 jours avant la date de fin de prêt
    @Scheduled(cron = "0 0 8 * * *") // tous les jours à 8h
    public void notifierPretsExpirantBientot() {
        List<PretLivre> pretsActifs = getAllPretsActifs();
        LocalDate dansDeuxJours = LocalDate.now().plusDays(2);
        for (PretLivre pret : pretsActifs) {
            if (pret.getDateFin() != null && pret.getDateFin().isEqual(dansDeuxJours)) {
                Notification notif = new Notification();
                notif.setAdherent(pret.getAdherent());
                notif.setTitre("Prêt arrivant à expiration");
                notif.setMessage("Votre prêt pour le livre '" + pret.getExemplaire().getLivre().getTitre() + "' expire dans 2 jours.");
                notif.setDateEnvoi(java.time.LocalDateTime.now());
                notif.setLu(false);
                notificationService.saveNotification(notif);
            }
        }
    }

    @Override
    public int countPretsEnCoursDomicileByAdherent(Adherent adherent) {
        return (int) pretLivreRepository.findByAdherentAndEtatPret(adherent, "en_cours").stream()
            .filter(pret -> pret.getTypePret() != null && pret.getTypePret().getNomTypePret() != null && pret.getTypePret().getNomTypePret().toLowerCase().contains("domicile"))
            .count();
    }

    @Override
    public int countPretsEnCoursSurPlaceByAdherent(Adherent adherent) {
        return (int) pretLivreRepository.findByAdherentAndEtatPret(adherent, "en_cours").stream()
            .filter(pret -> pret.getTypePret() != null && pret.getTypePret().getNomTypePret() != null && (pret.getTypePret().getNomTypePret().toLowerCase().contains("surplace") || pret.getTypePret().getNomTypePret().toLowerCase().contains("sur place")))
            .count();
    }
} 