package com.bibliotheque.service.impl;

import com.bibliotheque.model.Reservation;
import com.bibliotheque.model.Adherent;
import com.bibliotheque.model.Livre;
import com.bibliotheque.repository.ReservationRepository;
import com.bibliotheque.service.ReservationService;
import com.bibliotheque.service.AdherentService;
import com.bibliotheque.service.NotificationService;
import com.bibliotheque.model.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationServiceImpl implements ReservationService {
    private final ReservationRepository reservationRepository;
    private final AdherentService adherentService;
    private final NotificationService notificationService;

    @Autowired
    public ReservationServiceImpl(ReservationRepository reservationRepository, AdherentService adherentService, NotificationService notificationService) {
        this.reservationRepository = reservationRepository;
        this.adherentService = adherentService;
        this.notificationService = notificationService;
    }

    @Override
    public Reservation saveReservation(Reservation reservation) {
        if (reservation.getAdherent() != null && adherentService.isPenalise(reservation.getAdherent().getIdAdherent())) {
            throw new IllegalStateException("Cet adhérent est suspendu et ne peut pas réserver.");
        }
        boolean wasDisponible = false;
        if (reservation.getIdReservation() != null) {
            var oldOpt = reservationRepository.findById(reservation.getIdReservation());
            if (oldOpt.isPresent()) {
                wasDisponible = "disponible".equals(oldOpt.get().getEtatReservation());
            }
        }
        Reservation saved = reservationRepository.save(reservation);
        if ("disponible".equals(saved.getEtatReservation()) && !wasDisponible) {
            Notification notif = new Notification();
            notif.setAdherent(saved.getAdherent());
            notif.setTitre("Réservation disponible");
            notif.setMessage("Votre réservation est disponible. Merci de venir retirer le livre rapidement.");
            notif.setDateEnvoi(LocalDateTime.now());
            notif.setLu(false);
            notificationService.saveNotification(notif);
        }
        return saved;
    }

    @Override
    public Optional<Reservation> getReservationById(Long id) {
        return reservationRepository.findById(id);
    }

    @Override
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    @Override
    public List<Reservation> getReservationsByAdherent(Adherent adherent) {
        return reservationRepository.findByAdherent(adherent);
    }

    @Override
    public List<Reservation> getReservationsByLivre(Livre livre) {
        return reservationRepository.findByLivre(livre);
    }

    @Override
    public List<Reservation> getReservationsByEtat(String etatReservation) {
        return reservationRepository.findByEtatReservation(etatReservation);
    }

    @Override
    public List<Reservation> getReservationsByAdherentAndEtat(Adherent adherent, String etatReservation) {
        return reservationRepository.findByAdherentAndEtatReservation(adherent, etatReservation);
    }

    @Override
    public List<Reservation> getReservationsByLivreAndEtat(Livre livre, String etatReservation) {
        return reservationRepository.findByLivreAndEtatReservation(livre, etatReservation);
    }

    @Override
    public List<Reservation> getReservationsByAdherentAndLivre(Adherent adherent, Livre livre) {
        return reservationRepository.findByAdherentAndLivre(adherent, livre);
    }

    @Override
    public Optional<Reservation> getPremiereReservationActivePourLivre(Livre livre) {
        return reservationRepository.findFirstByLivreAndEtatReservationOrderByDateReservationAsc(livre, "active");
    }

    @Override
    public boolean existsActiveReservationForAdherentAndLivre(Adherent adherent, Livre livre) {
        return reservationRepository.existsByAdherentAndLivreAndEtatReservation(adherent, livre, "active");
    }

    @Override
    public List<Reservation> getReservationsExpireesAvant(LocalDate date) {
        return reservationRepository.findByDateReservationBeforeAndEtatReservation(date, "expiree");
    }

    @Override
    public void deleteReservation(Long id) {
        reservationRepository.deleteById(id);
    }
} 