package com.bibliotheque.repository;

import com.bibliotheque.model.Penalite;
import com.bibliotheque.model.PretLivre;
import com.bibliotheque.model.Adherent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PenaliteRepository extends JpaRepository<Penalite, Long> {
    List<Penalite> findByPretLivre_Adherent(Adherent adherent);
    List<Penalite> findByPretLivre(PretLivre pretLivre);
    Optional<Penalite> findFirstByPretLivreOrderByDateEmissionDesc(PretLivre pretLivre);
} 