package com.bibliotheque.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "retours_livre")
public class RetourLivre {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_retour")
    private Long idRetour;

    @OneToOne
    @JoinColumn(name = "id_pret_livre", nullable = false, unique = true)
    private PretLivre pretLivre;

    @NotNull(message = "La date de retour est obligatoire")
    @Column(name = "date_retour", nullable = false)
    private LocalDate dateRetour;

    @Column(name = "notes", length = 500)
    private String notes;

    @Column(name = "etat_retour", length = 50)
    private String etatRetour; // bon, endommage, perdu

    // Getters et Setters
    public Long getIdRetour() {
        return idRetour;
    }

    public void setIdRetour(Long idRetour) {
        this.idRetour = idRetour;
    }

    public PretLivre getPretLivre() {
        return pretLivre;
    }

    public void setPretLivre(PretLivre pretLivre) {
        this.pretLivre = pretLivre;
    }

    public LocalDate getDateRetour() {
        return dateRetour;
    }

    public void setDateRetour(LocalDate dateRetour) {
        this.dateRetour = dateRetour;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getEtatRetour() {
        return etatRetour;
    }

    public void setEtatRetour(String etatRetour) {
        this.etatRetour = etatRetour;
    }

    @PrePersist
    protected void onCreate() {
        if (dateRetour == null) {
            dateRetour = LocalDate.now();
        }
    }

    // Méthodes utilitaires
    public boolean estEnBonEtat() {
        return "bon".equalsIgnoreCase(etatRetour);
    }

    public boolean estEndommage() {
        return "endommage".equalsIgnoreCase(etatRetour);
    }

    public boolean estPerdu() {
        return "perdu".equalsIgnoreCase(etatRetour);
    }

    public boolean estEnRetard() {
        if (pretLivre == null || dateRetour == null) {
            return false;
        }
        return dateRetour.isAfter(pretLivre.getDateFin());
    }

    public int getJoursDeRetard() {
        if (!estEnRetard()) {
            return 0;
        }
        return (int) pretLivre.getDateFin().until(dateRetour).getDays();
    }
} 