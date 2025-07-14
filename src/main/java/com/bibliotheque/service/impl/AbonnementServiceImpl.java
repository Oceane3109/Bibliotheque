package com.bibliotheque.service.impl;

import com.bibliotheque.model.Abonnement;
import com.bibliotheque.model.Adherent;
import com.bibliotheque.repository.AbonnementRepository;
import com.bibliotheque.service.AbonnementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AbonnementServiceImpl implements AbonnementService {
    private final AbonnementRepository abonnementRepository;

    @Autowired
    public AbonnementServiceImpl(AbonnementRepository abonnementRepository) {
        this.abonnementRepository = abonnementRepository;
    }

    @Override
    public Abonnement saveAbonnement(Abonnement abonnement) {
        return abonnementRepository.save(abonnement);
    }

    @Override
    public Optional<Abonnement> getAbonnementById(Long id) {
        return abonnementRepository.findById(id);
    }

    @Override
    public List<Abonnement> getAbonnementsByAdherent(Adherent adherent) {
        return abonnementRepository.findByAdherent(adherent);
    }

    @Override
    public Optional<Abonnement> getAbonnementActifByAdherent(Adherent adherent) {
        return abonnementRepository.findFirstByAdherentAndStatutAndDateFinAfterOrderByDateFinDesc(
            adherent, "valide", LocalDate.now()
        );
    }

    @Override
    public List<Abonnement> getAbonnementsEnAttente() {
        return abonnementRepository.findByStatut("en_attente");
    }

    @Override
    public void validerAbonnement(Long idAbonnement) {
        abonnementRepository.findById(idAbonnement).ifPresent(abonnement -> {
            abonnement.setStatut("valide");
            abonnementRepository.save(abonnement);
        });
    }

    @Override
    public void refuserAbonnement(Long idAbonnement) {
        abonnementRepository.findById(idAbonnement).ifPresent(abonnement -> {
            abonnement.setStatut("refuse");
            abonnementRepository.save(abonnement);
        });
    }

    @Override
    public void expirerAbonnementsAutomatiquement() {
        List<Abonnement> aExpirer = abonnementRepository.findByStatutAndDateFinBefore("valide", LocalDate.now());
        for (Abonnement ab : aExpirer) {
            ab.setStatut("expire");
            abonnementRepository.save(ab);
        }
    }
} 