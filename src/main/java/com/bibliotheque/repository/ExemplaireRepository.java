package com.bibliotheque.repository;

import com.bibliotheque.model.Exemplaire;
import com.bibliotheque.model.Livre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExemplaireRepository extends JpaRepository<Exemplaire, Long> {
    Optional<Exemplaire> findByCodeExemplaire(String codeExemplaire);
    List<Exemplaire> findByLivre(Livre livre);
    List<Exemplaire> findByEtat(String etat);
    List<Exemplaire> findByLivreAndEtat(Livre livre, String etat);
    boolean existsByCodeExemplaire(String codeExemplaire);
}