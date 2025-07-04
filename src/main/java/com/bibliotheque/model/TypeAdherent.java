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
@Table(name = "type_adherent")
public class TypeAdherent {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_type_adherent")
    private Long idTypeAdherent;

    @NotBlank(message = "Le nom du type d'adhérent est obligatoire")
    @Column(name = "nom_type", nullable = false, length = 50)
    private String nomType;

    @Column(name = "description", length = 255)
    private String description;

    @OneToMany(mappedBy = "typeAdherent")
    private List<Adherent> adherents = new ArrayList<>();

    // Getters et Setters
    public Long getIdTypeAdherent() {
        return idTypeAdherent;
    }

    public void setIdTypeAdherent(Long idTypeAdherent) {
        this.idTypeAdherent = idTypeAdherent;
    }

    public String getNomType() {
        return nomType;
    }

    public void setNomType(String nomType) {
        this.nomType = nomType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Adherent> getAdherents() {
        return adherents;
    }

    public void setAdherents(List<Adherent> adherents) {
        this.adherents = adherents;
    }

    // Méthodes utilitaires
    public int getNombreAdherents() {
        return adherents.size();
    }

    public String getNomComplet() {
        if (description != null && !description.isEmpty()) {
            return nomType + " - " + description;
        }
        return nomType;
    }
} 