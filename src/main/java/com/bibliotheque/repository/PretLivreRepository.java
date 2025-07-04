package com.bibliotheque.repository;

import com.bibliotheque.model.PretLivre;
import com.bibliotheque.model.Adherent;
import com.bibliotheque.model.Exemplaire;
import com.bibliotheque.model.TypePret;
import org.springframework.data.jpa.repository.JpaRepository;
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
} 