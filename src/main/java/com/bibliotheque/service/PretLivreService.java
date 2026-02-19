package com.bibliotheque.service;

import com.bibliotheque.model.PretLivre;
import com.bibliotheque.model.Adherent;
import com.bibliotheque.model.Exemplaire;
import com.bibliotheque.model.TypePret;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PretLivreService {
    PretLivre savePret(PretLivre pretLivre);
    Optional<PretLivre> getPretById(Long id);
    Optional<PretLivre> getPretByIdWithRelations(Long id);
    List<PretLivre> getAllPrets();
    List<PretLivre> getPretsByAdherent(Adherent adherent);
    List<PretLivre> getPretsByExemplaire(Exemplaire exemplaire);
    List<PretLivre> getPretsByTypePret(TypePret typePret);
    List<PretLivre> getPretsByEtat(String etatPret);
    List<PretLivre> getPretsByAdherentAndEtat(Adherent adherent, String etatPret);
    List<PretLivre> getPretsByExemplaireAndEtat(Exemplaire exemplaire, String etatPret);
    List<PretLivre> getPretsEnRetardAvant(LocalDate date);
    Optional<PretLivre> getDernierPretActifPourExemplaire(Exemplaire exemplaire);
    
    // Nouvelles méthodes pour gérer les prêts actifs/inactifs
    List<PretLivre> getPretsActifsByAdherent(Adherent adherent);
    List<PretLivre> getPretsInactifsByAdherent(Adherent adherent);
    List<PretLivre> getAllPretsActifs();
    List<PretLivre> getAllPretsInactifs();
    void marquerPretCommeRetourne(Long idPret);
    void marquerPretCommeRetourne(Long idPret, java.time.LocalDate dateRetourEffective);
    
    int countPretsEnCoursDomicileByAdherent(com.bibliotheque.model.Adherent adherent);
    int countPretsEnCoursSurPlaceByAdherent(com.bibliotheque.model.Adherent adherent);
    
    void deletePret(Long id);
} 