package com.bibliotheque.repository;

import com.bibliotheque.model.ProlongementPret;
import com.bibliotheque.model.PretLivre;
import com.bibliotheque.model.Adherent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProlongementPretRepository extends JpaRepository<ProlongementPret, Long> {
    List<ProlongementPret> findByPretLivre(PretLivre pretLivre);
    List<ProlongementPret> findByEtatProlongation(String etatProlongation);
    List<ProlongementPret> findByPretLivreAndEtatProlongation(PretLivre pretLivre, String etatProlongation);
    Optional<ProlongementPret> findFirstByPretLivreOrderByDateDemandeDesc(PretLivre pretLivre);
    @Query("SELECT p FROM ProlongementPret p JOIN FETCH p.pretLivre pr JOIN FETCH pr.exemplaire e JOIN FETCH e.livre WHERE pr.adherent = :adherent")
    List<ProlongementPret> findByPretLivre_Adherent(@Param("adherent") Adherent adherent);
    int countByPretLivre_AdherentAndEtatProlongation(Adherent adherent, String etatProlongation);
    
    @Query("SELECT p FROM ProlongementPret p " +
           "JOIN FETCH p.pretLivre pr " +
           "JOIN FETCH pr.adherent a " +
           "JOIN FETCH pr.exemplaire e " +
           "JOIN FETCH e.livre l " +
           "ORDER BY p.dateDemande DESC")
    List<ProlongementPret> findAllWithRelations();
} 