package com.bibliotheque.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "prets_livre")
public class PretLivre {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pret")
    private Long idPret;

    @ManyToOne
    @JoinColumn(name = "id_adherent", nullable = false)
    private Adherent adherent;

    @ManyToOne
    @JoinColumn(name = "id_exemplaire", nullable = false)
    private Exemplaire exemplaire;

    @ManyToOne
    @JoinColumn(name = "id_type_pret", nullable = false)
    private TypePret typePret;

    @NotNull(message = "La date de début est obligatoire")
    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @NotNull(message = "La date de fin est obligatoire")
    @Column(name = "date_fin", nullable = false)
    private LocalDate dateFin;

    @Column(name = "date_retour_effective")
    private LocalDate dateRetourEffective;

    @NotBlank(message = "L'état du prêt est obligatoire")
    @Column(name = "etat_pret", nullable = false, length = 50)
    private String etatPret; // en_cours, retourne, en_retard

    @OneToMany(mappedBy = "pretLivre")
    private List<ProlongementPret> prolongements = new ArrayList<>();

    @OneToMany(mappedBy = "pretLivre")
    private List<Penalite> penalites = new ArrayList<>();

    @OneToOne(mappedBy = "pretLivre", cascade = CascadeType.ALL)
    private RetourLivre retourLivre;

    // Getters et Setters
    public Long getIdPret() {
        return idPret;
    }

    public void setIdPret(Long idPret) {
        this.idPret = idPret;
    }

    public Adherent getAdherent() {
        return adherent;
    }

    public void setAdherent(Adherent adherent) {
        this.adherent = adherent;
    }

    public Exemplaire getExemplaire() {
        return exemplaire;
    }

    public void setExemplaire(Exemplaire exemplaire) {
        this.exemplaire = exemplaire;
    }

    public TypePret getTypePret() {
        return typePret;
    }

    public void setTypePret(TypePret typePret) {
        this.typePret = typePret;
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

    public LocalDate getDateRetourEffective() {
        return dateRetourEffective;
    }

    public void setDateRetourEffective(LocalDate dateRetourEffective) {
        this.dateRetourEffective = dateRetourEffective;
    }

    public String getEtatPret() {
        return etatPret;
    }

    public void setEtatPret(String etatPret) {
        this.etatPret = etatPret;
    }

    public List<ProlongementPret> getProlongements() {
        return prolongements;
    }

    public void setProlongements(List<ProlongementPret> prolongements) {
        this.prolongements = prolongements;
    }

    public List<Penalite> getPenalites() {
        return penalites;
    }

    public void setPenalites(List<Penalite> penalites) {
        this.penalites = penalites;
    }

    public RetourLivre getRetourLivre() {
        return retourLivre;
    }

    public void setRetourLivre(RetourLivre retourLivre) {
        this.retourLivre = retourLivre;
    }

    // Méthodes utilitaires
    public boolean estEnCours() {
        return "en_cours".equalsIgnoreCase(etatPret);
    }

    public boolean estRetourne() {
        return "retourne".equalsIgnoreCase(etatPret);
    }

    public boolean estEnRetard() {
        return "en_retard".equalsIgnoreCase(etatPret);
    }

    public boolean estEnRetard(LocalDate dateReference) {
        return estEnCours() && dateFin.isBefore(dateReference);
    }

    public int getJoursDeRetard(LocalDate dateReference) {
        if (!estEnRetard(dateReference)) {
            return 0;
        }
        return (int) dateFin.until(dateReference).getDays();
    }

    public boolean peutEtreProlonge() {
        return estEnCours() && !estEnRetard() && prolongements.stream()
                .noneMatch(p -> "approuve".equalsIgnoreCase(p.getEtatProlongation()));
    }
}