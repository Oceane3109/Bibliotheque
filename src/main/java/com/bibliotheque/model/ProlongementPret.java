package com.bibliotheque.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "prolongements_pret")
public class ProlongementPret {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_prolongation")
    private Long idProlongation;

    @ManyToOne
    @JoinColumn(name = "id_pret", nullable = false)
    private PretLivre pretLivre;

    @NotNull(message = "La date de demande est obligatoire")
    @Column(name = "date_demande", nullable = false)
    private LocalDate dateDemande;

    @NotNull(message = "La nouvelle date de fin est obligatoire")
    @Column(name = "nouvelle_date_fin", nullable = false)
    private LocalDate nouvelleDateFin;

    @NotBlank(message = "L'état de la prolongation est obligatoire")
    @Column(name = "etat_prolongation", nullable = false, length = 50)
    private String etatProlongation; // en_attente, approuvee, refusee

    // Getters et Setters
    public Long getIdProlongation() {
        return idProlongation;
    }

    public void setIdProlongation(Long idProlongation) {
        this.idProlongation = idProlongation;
    }

    public PretLivre getPretLivre() {
        return pretLivre;
    }

    public void setPretLivre(PretLivre pretLivre) {
        this.pretLivre = pretLivre;
    }

    public LocalDate getDateDemande() {
        return dateDemande;
    }

    public void setDateDemande(LocalDate dateDemande) {
        this.dateDemande = dateDemande;
    }

    public LocalDate getNouvelleDateFin() {
        return nouvelleDateFin;
    }

    public void setNouvelleDateFin(LocalDate nouvelleDateFin) {
        this.nouvelleDateFin = nouvelleDateFin;
    }

    public String getEtatProlongation() {
        return etatProlongation;
    }

    public void setEtatProlongation(String etatProlongation) {
        this.etatProlongation = etatProlongation;
    }

    @PrePersist
    protected void onCreate() {
        if (dateDemande == null) {
            dateDemande = LocalDate.now();
        }
    }

    // Méthodes utilitaires
    public boolean estEnAttente() {
        return "en_attente".equalsIgnoreCase(etatProlongation);
    }

    public boolean estApprouvee() {
        return "approuvee".equalsIgnoreCase(etatProlongation);
    }

    public boolean estRefusee() {
        return "refusee".equalsIgnoreCase(etatProlongation);
    }

    public boolean estTraitee() {
        return estApprouvee() || estRefusee();
    }

    public int getJoursAttente(LocalDate dateReference) {
        if (!estEnAttente() || dateDemande == null) {
            return 0;
        }
        return (int) dateDemande.until(dateReference).getDays();
    }
} 