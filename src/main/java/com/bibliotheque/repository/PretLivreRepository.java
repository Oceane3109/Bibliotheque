package com.bibliotheque.repository;

import com.bibliotheque.model.PretLivre;
import com.bibliotheque.model.Adherent;
import com.bibliotheque.model.Exemplaire;
import com.bibliotheque.model.TypePret;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PretLivreRepository extends JpaRepository<PretLivre, Long> {
    List<PretLivre> findByAdherent(Adherent adherent);
    List<PretLivre> findByExemplaire(Exemplaire exemplaire);
    List<PretLivre> findByTypePret(TypePret typePret);
    List<PretLivre> findByEtatPret(String etatPret);
    List<PretLivre> findByAdherentAndEtatPret(Adherent adherent, String etatPret);
    List<PretLivre> findByExemplaireAndEtatPret(Exemplaire exemplaire, String etatPret);
    List<PretLivre> findByDateFinBeforeAndEtatPret(LocalDate date, String etatPret);
    Optional<PretLivre> findFirstByExemplaireAndEtatPretOrderByDateDebutDesc(Exemplaire exemplaire, String etatPret);
    
    // Supprimer les méthodes suivantes car on harmonise sur etatPret
    // List<PretLivre> findByAdherentAndActifOrderByDateDebutDesc(Adherent adherent, Boolean actif);
    // List<PretLivre> findByActifOrderByDateDebutDesc(Boolean actif);
    List<PretLivre> findByAdherentOrderByDateDebutDesc(Adherent adherent);
    
    @Query("SELECT p FROM PretLivre p " +
           "LEFT JOIN FETCH p.adherent a " +
           "LEFT JOIN FETCH a.typeAdherent " +
           "LEFT JOIN FETCH a.user " +
           "LEFT JOIN FETCH p.exemplaire e " +
           "LEFT JOIN FETCH e.livre " +
           "LEFT JOIN FETCH p.typePret " +
           "WHERE p.idPret = :id")
    Optional<PretLivre> findByIdWithRelations(@Param("id") Long id);
} 