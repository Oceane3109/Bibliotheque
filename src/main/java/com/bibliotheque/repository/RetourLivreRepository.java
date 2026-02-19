package com.bibliotheque.repository;

import com.bibliotheque.model.RetourLivre;
import com.bibliotheque.model.PretLivre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RetourLivreRepository extends JpaRepository<RetourLivre, Long> {
    List<RetourLivre> findByPretLivre(PretLivre pretLivre);
    Optional<RetourLivre> findFirstByPretLivreOrderByDateRetourDesc(PretLivre pretLivre);
} 