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
@Table(name = "livres")
public class Livre {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_livre")
    private Long idLivre;

    @NotBlank(message = "Le titre est obligatoire")
    @Column(name = "titre", nullable = false, length = 255)
    private String titre;

    @NotBlank(message = "L'auteur est obligatoire")
    @Column(name = "auteur", nullable = false, length = 100)
    private String auteur;

    @Column(name = "editeur", length = 100)
    private String editeur;

    @Min(value = 1000, message = "L'année de publication doit être valide")
    @Max(value = 2100, message = "L'année de publication doit être valide")
    @Column(name = "annee_publication")
    private Integer anneePublication;

    @Pattern(regexp = "^[0-9X-]{10,13}$", message = "L'ISBN doit être valide")
    @Column(name = "isbn", unique = true, length = 13)
    private String isbn;

    @Min(value = 0, message = "L'âge minimum doit être positif")
    @Column(name = "age_minimum")
    private Integer ageMinimum;

    @Column(name = "image_nom")
    private String imageNom;

    @Column(name = "image_type")
    private String imageType;

    @Lob
    @Column(name = "image_donnees")
    private byte[] imageDonnees;

    @Column(name = "image_url")
    private String imageUrl;

    @OneToMany(mappedBy = "livre", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Exemplaire> exemplaires = new ArrayList<>();

    @OneToMany(mappedBy = "livre")
    private List<Reservation> reservations = new ArrayList<>();

    // Getters et Setters
    public Long getIdLivre() {
        return idLivre;
    }

    public void setIdLivre(Long idLivre) {
        this.idLivre = idLivre;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getAuteur() {
        return auteur;
    }

    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }

    public String getEditeur() {
        return editeur;
    }

    public void setEditeur(String editeur) {
        this.editeur = editeur;
    }

    public Integer getAnneePublication() {
        return anneePublication;
    }

    public void setAnneePublication(Integer anneePublication) {
        this.anneePublication = anneePublication;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Integer getAgeMinimum() {
        return ageMinimum;
    }

    public void setAgeMinimum(Integer ageMinimum) {
        this.ageMinimum = ageMinimum;
    }

    public String getImageNom() {
        return imageNom;
    }

    public void setImageNom(String imageNom) {
        this.imageNom = imageNom;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public byte[] getImageDonnees() {
        return imageDonnees;
    }

    public void setImageDonnees(byte[] imageDonnees) {
        this.imageDonnees = imageDonnees;
    }

    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<Exemplaire> getExemplaires() {
        return exemplaires;
    }

    public void setExemplaires(List<Exemplaire> exemplaires) {
        this.exemplaires = exemplaires;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    // Méthodes utilitaires
    public long getNombreExemplairesDisponibles() {
        return exemplaires.stream()
                .filter(Exemplaire::estDisponible)
                .count();
    }

    public long getNombreExemplairesEnPret() {
        return exemplaires.stream()
                .filter(Exemplaire::estEnPret)
                .count();
    }

    public boolean aExemplairesDisponibles() {
        return getNombreExemplairesDisponibles() > 0;
    }

    public String getTitreComplet() {
        return titre + " - " + auteur;
    }

    public String getImageContentType() {
        return imageType;
    }

    public void setImage(String nom, String type, byte[] donnees) {
        this.imageNom = nom;
        this.imageType = type;
        this.imageDonnees = donnees;
    }

    public void supprimerImage() {
        this.imageNom = null;
        this.imageType = null;
        this.imageDonnees = null;
    }

    public boolean hasImage() {
        return imageNom != null && imageDonnees != null && imageDonnees.length > 0;
    }
} 