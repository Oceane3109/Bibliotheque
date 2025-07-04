package com.bibliotheque.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private Long idUser;

    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    @Column(name = "nom_utilisateur", nullable = false, unique = true, length = 50)
    private String nomUtilisateur;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Column(name = "mot_de_passe", nullable = false)
    private String motDePasse;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @NotNull(message = "La date de création est obligatoire")
    @Column(name = "date_creation", nullable = false)
    private LocalDate dateCreation;

    @Column(name = "est_admin", nullable = false)
    private Boolean estAdmin = false;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Adherent adherent;

    // Getters et Setters
    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    public String getNomUtilisateur() {
        return nomUtilisateur;
    }

    public void setNomUtilisateur(String nomUtilisateur) {
        this.nomUtilisateur = nomUtilisateur;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDate dateCreation) {
        this.dateCreation = dateCreation;
    }

    public Boolean getEstAdmin() {
        return estAdmin;
    }

    public void setEstAdmin(Boolean estAdmin) {
        this.estAdmin = estAdmin;
    }

    public Adherent getAdherent() {
        return adherent;
    }

    public void setAdherent(Adherent adherent) {
        this.adherent = adherent;
    }

    @PrePersist
    protected void onCreate() {
        if (dateCreation == null) {
            dateCreation = LocalDate.now();
        }
        if (estAdmin == null) {
            estAdmin = false;
        }
    }

    // Méthodes utilitaires
    public boolean isAdmin() {
        return estAdmin != null && estAdmin;
    }

    public boolean hasAdherent() {
        return adherent != null;
    }
} 