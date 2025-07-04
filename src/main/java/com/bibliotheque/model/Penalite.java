package com.bibliotheque.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "penalites")
public class Penalite {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_penalite")
    private Long idPenalite;

    @ManyToOne
    @JoinColumn(name = "id_pret_livre", nullable = false)
    private PretLivre pretLivre;

    @NotBlank(message = "Le motif est obligatoire")
    @Column(name = "motif", nullable = false, length = 500)
    private String motif;

    @NotNull(message = "La date d'émission est obligatoire")
    @Column(name = "date_emission", nullable = false)
    private LocalDate dateEmission;

    @Min(value = 1, message = "Le nombre de jours de pénalité doit être positif")
    @Column(name = "jours_penalite", nullable = false)
    private Integer joursPenalite;

    // Getters et Setters
    public Long getIdPenalite() {
        return idPenalite;
    }

    public void setIdPenalite(Long idPenalite) {
        this.idPenalite = idPenalite;
    }

    public PretLivre getPretLivre() {
        return pretLivre;
    }

    public void setPretLivre(PretLivre pretLivre) {
        this.pretLivre = pretLivre;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public LocalDate getDateEmission() {
        return dateEmission;
    }

    public void setDateEmission(LocalDate dateEmission) {
        this.dateEmission = dateEmission;
    }

    public Integer getJoursPenalite() {
        return joursPenalite;
    }

    public void setJoursPenalite(Integer joursPenalite) {
        this.joursPenalite = joursPenalite;
    }

    @PrePersist
    protected void onCreate() {
        if (dateEmission == null) {
            dateEmission = LocalDate.now();
        }
    }

    // Méthodes utilitaires
    public LocalDate getDateFinPenalite() {
        if (dateEmission == null || joursPenalite == null) {
            return null;
        }
        return dateEmission.plusDays(joursPenalite);
    }

    public boolean estEnCours(LocalDate dateReference) {
        LocalDate dateFin = getDateFinPenalite();
        return dateFin != null && !dateFin.isBefore(dateReference);
    }

    public boolean estExpiree(LocalDate dateReference) {
        LocalDate dateFin = getDateFinPenalite();
        return dateFin != null && dateFin.isBefore(dateReference);
    }

    public int getJoursRestants(LocalDate dateReference) {
        if (estExpiree(dateReference)) {
            return 0;
        }
        LocalDate dateFin = getDateFinPenalite();
        if (dateFin == null) {
            return 0;
        }
        return (int) dateReference.until(dateFin).getDays();
    }
} 