package com.bibliotheque.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reservations")
public class Reservation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reservation")
    private Long idReservation;

    @ManyToOne
    @JoinColumn(name = "id_adherent", nullable = false)
    private Adherent adherent;

    @ManyToOne
    @JoinColumn(name = "id_livre", nullable = false)
    private Livre livre;

    @ManyToOne
    @JoinColumn(name = "id_type_pret", nullable = false)
    private TypePret typePret;

    @NotNull(message = "La date de réservation est obligatoire")
    @Column(name = "date_reservation", nullable = false)
    private LocalDate dateReservation;

    @Column(name = "date_disponibilite")
    private LocalDate dateDisponibilite;

    @NotNull(message = "La date de prêt est obligatoire")
    @Column(name = "date_pret", nullable = false)
    private LocalDate datePret;

    @NotNull(message = "La date de fin de prêt est obligatoire")
    @Column(name = "date_fin_pret", nullable = false)
    private LocalDate dateFinPret;

    @NotBlank(message = "L'état de la réservation est obligatoire")
    @Column(name = "etat_reservation", nullable = false, length = 50)
    private String etatReservation; // en_attente, disponible, expiree, annulee

    // Getters et Setters
    public Long getIdReservation() {
        return idReservation;
    }

    public void setIdReservation(Long idReservation) {
        this.idReservation = idReservation;
    }

    public Adherent getAdherent() {
        return adherent;
    }

    public void setAdherent(Adherent adherent) {
        this.adherent = adherent;
    }

    public Livre getLivre() {
        return livre;
    }

    public void setLivre(Livre livre) {
        this.livre = livre;
    }

    public TypePret getTypePret() {
        return typePret;
    }
    public void setTypePret(TypePret typePret) {
        this.typePret = typePret;
    }

    public LocalDate getDateReservation() {
        return dateReservation;
    }

    public void setDateReservation(LocalDate dateReservation) {
        this.dateReservation = dateReservation;
    }

    public LocalDate getDateDisponibilite() {
        return dateDisponibilite;
    }

    public void setDateDisponibilite(LocalDate dateDisponibilite) {
        this.dateDisponibilite = dateDisponibilite;
    }

    public LocalDate getDatePret() {
        return datePret;
    }

    public void setDatePret(LocalDate datePret) {
        this.datePret = datePret;
    }

    public LocalDate getDateFinPret() {
        return dateFinPret;
    }

    public void setDateFinPret(LocalDate dateFinPret) {
        this.dateFinPret = dateFinPret;
    }

    public String getEtatReservation() {
        return etatReservation;
    }

    public void setEtatReservation(String etatReservation) {
        this.etatReservation = etatReservation;
    }

    @PrePersist
    protected void onCreate() {
        if (dateReservation == null) {
            dateReservation = LocalDate.now();
        }
    }

    // Méthodes utilitaires
    public boolean estEnAttente() {
        return "en_attente".equalsIgnoreCase(etatReservation);
    }

    public boolean estDisponible() {
        return "disponible".equalsIgnoreCase(etatReservation);
    }

    public boolean estExpiree() {
        return "expiree".equalsIgnoreCase(etatReservation);
    }

    public boolean estAnnulee() {
        return "annulee".equalsIgnoreCase(etatReservation);
    }

    public boolean estExpiree(LocalDate dateReference) {
        return estEnAttente() && dateReservation.plusDays(7).isBefore(dateReference);
    }

    public int getJoursAttente(LocalDate dateReference) {
        if (!estEnAttente()) {
            return 0;
        }
        return (int) dateReservation.until(dateReference).getDays();
    }
} 