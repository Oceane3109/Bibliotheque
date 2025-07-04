package com.bibliotheque.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "types_pret")
public class TypePret {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_type_pret")
    private Long idTypePret;

    @NotBlank(message = "Le nom du type de prêt est obligatoire")
    @Column(name = "nom_type_pret", nullable = false, length = 50)
    private String nomTypePret;

    @Column(name = "description", length = 255)
    private String description;

    @OneToMany(mappedBy = "typePret")
    private List<PretLivre> prets = new ArrayList<>();

    // Getters et Setters
    public Long getIdTypePret() {
        return idTypePret;
    }

    public void setIdTypePret(Long idTypePret) {
        this.idTypePret = idTypePret;
    }

    public String getNomTypePret() {
        return nomTypePret;
    }

    public void setNomTypePret(String nomTypePret) {
        this.nomTypePret = nomTypePret;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<PretLivre> getPrets() {
        return prets;
    }

    public void setPrets(List<PretLivre> prets) {
        this.prets = prets;
    }

    // Méthodes utilitaires
    public int getNombrePrets() {
        return prets.size();
    }

    public String getNomComplet() {
        if (description != null && !description.isEmpty()) {
            return nomTypePret + " - " + description;
        }
        return nomTypePret;
    }
} 