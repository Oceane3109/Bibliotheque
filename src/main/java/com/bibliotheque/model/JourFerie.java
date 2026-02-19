package com.bibliotheque.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "jours_feries")
public class JourFerie {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_jour_ferie")
    private Long idJourFerie;

    @NotBlank(message = "Le nom du jour férié est obligatoire")
    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @NotNull(message = "La date est obligatoire")
    @Column(name = "date_feriee", nullable = false, unique = true)
    private LocalDate dateFeriee;

    @Column(name = "description", length = 255)
    private String description;

    // Getters et Setters
    public Long getIdJourFerie() {
        return idJourFerie;
    }

    public void setIdJourFerie(Long idJourFerie) {
        this.idJourFerie = idJourFerie;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public LocalDate getDateFeriee() {
        return dateFeriee;
    }

    public void setDateFeriee(LocalDate dateFeriee) {
        this.dateFeriee = dateFeriee;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Méthodes utilitaires
    public boolean estDansLeFutur(LocalDate dateReference) {
        return dateFeriee.isAfter(dateReference);
    }

    public boolean estDansLePasse(LocalDate dateReference) {
        return dateFeriee.isBefore(dateReference);
    }

    public boolean estAujourdhui(LocalDate dateReference) {
        return dateFeriee.equals(dateReference);
    }

    public String getNomComplet() {
        if (description != null && !description.isEmpty()) {
            return nom + " - " + description;
        }
        return nom;
    }
} 