package com.bibliotheque.service;

import com.bibliotheque.model.Reservation;
import com.bibliotheque.model.Adherent;
import com.bibliotheque.model.Livre;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservationService {
    Reservation saveReservation(Reservation reservation);
    Optional<Reservation> getReservationById(Long id);
    List<Reservation> getAllReservations();
    List<Reservation> getReservationsByAdherent(Adherent adherent);
    List<Reservation> getReservationsByLivre(Livre livre);
    List<Reservation> getReservationsByEtat(String etatReservation);
    List<Reservation> getReservationsByAdherentAndEtat(Adherent adherent, String etatReservation);
    List<Reservation> getReservationsByLivreAndEtat(Livre livre, String etatReservation);
    List<Reservation> getReservationsByAdherentAndLivre(Adherent adherent, Livre livre);
    Optional<Reservation> getPremiereReservationActivePourLivre(Livre livre);
    boolean existsActiveReservationForAdherentAndLivre(Adherent adherent, Livre livre);
    List<Reservation> getReservationsExpireesAvant(LocalDate date);
    void deleteReservation(Long id);
} 