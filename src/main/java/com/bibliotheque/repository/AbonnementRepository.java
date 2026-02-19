package com.bibliotheque.repository;

import com.bibliotheque.model.Abonnement;
import com.bibliotheque.model.Adherent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AbonnementRepository extends JpaRepository<Abonnement, Long> {
    List<Abonnement> findByAdherent(Adherent adherent);
    Optional<Abonnement> findFirstByAdherentAndStatutOrderByDateFinDesc(Adherent adherent, String statut);
    List<Abonnement> findByStatut(String statut);
    List<Abonnement> findByStatutAndDateFinBefore(String statut, LocalDate date);
    Optional<Abonnement> findFirstByAdherentAndStatutAndDateFinAfterOrderByDateFinDesc(Adherent adherent, String statut, LocalDate date);
} 