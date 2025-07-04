package com.bibliotheque.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users_penalises")
public class UserPenalise {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user_penalise")
    private Long idUserPenalise;

    @ManyToOne
    @JoinColumn(name = "id_adherent", nullable = false)
    private Adherent adherent;

    @NotNull(message = "La date de début est obligatoire")
    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @NotNull(message = "La date de fin est obligatoire")
    @Column(name = "date_fin", nullable = false)
    private LocalDate dateFin;

    @NotBlank(message = "Le motif est obligatoire")
    @Column(name = "motif", nullable = false, length = 500)
    private String motif;

    @Column(name = "actif", nullable = false)
    private Boolean actif = true;

    // Getters et Setters
    public Long getIdUserPenalise() {
        return idUserPenalise;
    }

    public void setIdUserPenalise(Long idUserPenalise) {
        this.idUserPenalise = idUserPenalise;
    }

    public Adherent getAdherent() {
        return adherent;
    }

    public void setAdherent(Adherent adherent) {
        this.adherent = adherent;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public Boolean getActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }

    @PrePersist
    protected void onCreate() {
        if (actif == null) {
            actif = true;
        }
    }

    // Méthodes utilitaires
    public boolean isActif() {
        return actif != null && actif;
    }

    public boolean estExpiree(LocalDate dateReference) {
        return dateFin.isBefore(dateReference);
    }

    public boolean estEnCours(LocalDate dateReference) {
        return isActif() && !dateDebut.isAfter(dateReference) && !dateFin.isBefore(dateReference);
    }

    public int getJoursRestants(LocalDate dateReference) {
        if (estExpiree(dateReference)) {
            return 0;
        }
        return (int) dateReference.until(dateFin).getDays();
    }
} 