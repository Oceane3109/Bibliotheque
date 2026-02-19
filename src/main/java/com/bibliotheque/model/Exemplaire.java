package com.bibliotheque.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "exemplaires")
public class Exemplaire {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_exemplaire")
    private Long idExemplaire;

    @ManyToOne
    @JoinColumn(name = "id_livre", nullable = false)
    @JsonBackReference
    private Livre livre;

    @NotBlank(message = "Le code exemplaire est obligatoire")
    @Column(name = "code_exemplaire", nullable = false, unique = true, length = 20)
    private String codeExemplaire;

    @NotBlank(message = "L'état est obligatoire")
    @Column(name = "etat", nullable = false, length = 50)
    private String etat;

    @Column(name = "notes", length = 500)
    private String notes;

    @OneToMany(mappedBy = "exemplaire")
    @JsonIgnore
    private List<PretLivre> prets = new ArrayList<>();

    // Getters et Setters
    public Long getIdExemplaire() {
        return idExemplaire;
    }

    public void setIdExemplaire(Long idExemplaire) {
        this.idExemplaire = idExemplaire;
    }

    public Livre getLivre() {
        return livre;
    }

    public void setLivre(Livre livre) {
        this.livre = livre;
    }

    public String getCodeExemplaire() {
        return codeExemplaire;
    }

    public void setCodeExemplaire(String codeExemplaire) {
        this.codeExemplaire = codeExemplaire;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<PretLivre> getPrets() {
        return prets;
    }

    public void setPrets(List<PretLivre> prets) {
        this.prets = prets;
    }

    // Méthodes utilitaires
    public boolean estDisponible() {
        return "disponible".equalsIgnoreCase(etat);
    }

    public boolean estEnPret() {
        return "en_pret".equalsIgnoreCase(etat);
    }

    public boolean estEnReparation() {
        return "en_reparation".equalsIgnoreCase(etat);
    }

    public boolean estPerdu() {
        return "perdu".equalsIgnoreCase(etat);
    }
} 