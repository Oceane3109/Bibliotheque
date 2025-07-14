package com.bibliotheque.service;

import com.bibliotheque.model.ProlongementPret;
import com.bibliotheque.model.PretLivre;
import com.bibliotheque.model.Adherent;
import java.util.List;
import java.util.Optional;

public interface ProlongementPretService {
    ProlongementPret saveProlongement(ProlongementPret prolongementPret);
    Optional<ProlongementPret> getProlongementById(Long id);
    List<ProlongementPret> getAllProlongements();
    List<ProlongementPret> getProlongementsByPret(PretLivre pretLivre);
    List<ProlongementPret> getProlongementsByEtat(String etatProlongation);
    List<ProlongementPret> getProlongementsByPretAndEtat(PretLivre pretLivre, String etatProlongation);
    Optional<ProlongementPret> getDernierProlongementPourPret(PretLivre pretLivre);
    void deleteProlongement(Long id);
    List<ProlongementPret> getProlongementsByAdherent(Adherent adherent);
    int getNombreProlongementsApprouvesByAdherent(Adherent adherent);
    List<ProlongementPret> getAllProlongementsWithRelations();
} 