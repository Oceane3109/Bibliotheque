package com.bibliotheque.service;

import com.bibliotheque.model.Abonnement;
import com.bibliotheque.model.Adherent;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AbonnementService {
    Abonnement saveAbonnement(Abonnement abonnement);
    Optional<Abonnement> getAbonnementById(Long id);
    List<Abonnement> getAbonnementsByAdherent(Adherent adherent);
    Optional<Abonnement> getAbonnementActifByAdherent(Adherent adherent);
    List<Abonnement> getAbonnementsEnAttente();
    void validerAbonnement(Long idAbonnement);
    void refuserAbonnement(Long idAbonnement);
    void expirerAbonnementsAutomatiquement();
} 