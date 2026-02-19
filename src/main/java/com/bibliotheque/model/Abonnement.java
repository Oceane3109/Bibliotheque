package com.bibliotheque.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Abonnement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAbonnement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_adherent", nullable = false)
    private Adherent adherent;

    @Column(nullable = false)
    private LocalDate dateDebut;

    @Column(nullable = false)
    private LocalDate dateFin;

    @Column(nullable = false)
    private String statut; // en_attente, valide, refuse, expire

    // Getters et setters
    public Long getIdAbonnement() { return idAbonnement; }
    public void setIdAbonnement(Long idAbonnement) { this.idAbonnement = idAbonnement; }

    public Adherent getAdherent() { return adherent; }
    public void setAdherent(Adherent adherent) { this.adherent = adherent; }

    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }

    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    // Utilitaire : savoir si l'abonnement est actif
    public boolean isActif() {
        return "valide".equalsIgnoreCase(statut) &&
               (dateFin == null || !dateFin.isBefore(LocalDate.now()));
    }
} 