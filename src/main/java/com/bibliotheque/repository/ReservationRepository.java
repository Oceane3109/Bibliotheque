package com.bibliotheque.repository;

import com.bibliotheque.model.Reservation;
import com.bibliotheque.model.Adherent;
import com.bibliotheque.model.Livre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByAdherent(Adherent adherent);
    List<Reservation> findByLivre(Livre livre);
    List<Reservation> findByEtatReservation(String etatReservation);
    List<Reservation> findByAdherentAndEtatReservation(Adherent adherent, String etatReservation);
    List<Reservation> findByLivreAndEtatReservation(Livre livre, String etatReservation);
    List<Reservation> findByAdherentAndLivre(Adherent adherent, Livre livre);
    Optional<Reservation> findFirstByLivreAndEtatReservationOrderByDateReservationAsc(Livre livre, String etatReservation);
    boolean existsByAdherentAndLivreAndEtatReservation(Adherent adherent, Livre livre, String etatReservation);
    List<Reservation> findByDateReservationBeforeAndEtatReservation(LocalDate date, String etatReservation);
} 