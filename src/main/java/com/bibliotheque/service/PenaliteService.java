package com.bibliotheque.service;

import com.bibliotheque.model.Penalite;
import com.bibliotheque.model.PretLivre;
import com.bibliotheque.model.Adherent;
import java.util.List;
import java.util.Optional;

public interface PenaliteService {
    Penalite savePenalite(Penalite penalite);
    Optional<Penalite> getPenaliteById(Long id);
    List<Penalite> getAllPenalites();
    List<Penalite> getPenalitesByAdherent(Adherent adherent);
    List<Penalite> getPenalitesByPret(PretLivre pretLivre);
    Optional<Penalite> getDernierePenalitePourPret(PretLivre pretLivre);
    void deletePenalite(Long id);
} 