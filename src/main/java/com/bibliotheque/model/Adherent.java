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
@Table(name = "adherents")
public class Adherent {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_adherent")
    private Long idAdherent;

    @OneToOne
    @JoinColumn(name = "id_user", unique = true, nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "id_type_adherent", nullable = false)
    private TypeAdherent typeAdherent;

    @NotBlank(message = "Le nom est obligatoire")
    @Column(name = "nom", nullable = false, length = 50)
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @Column(name = "prenom", nullable = false, length = 50)
    private String prenom;

    @Column(name = "adresse", length = 255)
    private String adresse;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    @Column(name = "email", nullable = false, length = 100, unique = true)
    private String email;

    @Pattern(regexp = "^[0-9]{10}$", message = "Le numéro de téléphone doit contenir 10 chiffres")
    @Column(name = "telephone", length = 20)
    private String telephone;

    @NotNull(message = "La date d'inscription est obligatoire")
    @Column(name = "date_inscription", nullable = false)
    private LocalDate dateInscription;

    @NotNull(message = "La date de naissance est obligatoire")
    @Past(message = "La date de naissance doit être dans le passé")
    @Column(name = "date_naissance", nullable = false)
    private LocalDate dateNaissance;

    @Min(value = 0, message = "Le nombre maximum de livres à domicile doit être positif")
    @Column(name = "max_livres_domicile", nullable = false)
    private Integer maxLivresDomicile = 3;

    @Min(value = 0, message = "Le nombre maximum de livres sur place doit être positif")
    @Column(name = "max_livres_surplace", nullable = false)
    private Integer maxLivresSurplace = 3;

    @Min(value = 0, message = "Le nombre de livres empruntés à domicile doit être positif")
    @Column(name = "livres_empruntes_domicile", nullable = false)
    private Integer livresEmpruntesDomicile = 0;

    @Min(value = 0, message = "Le nombre de livres empruntés sur place doit être positif")
    @Column(name = "livres_empruntes_surplace", nullable = false)
    private Integer livresEmpruntesSurplace = 0;

    @Min(value = 1, message = "La durée de prêt doit être d'au moins 1 jour")
    @Column(name = "duree_pret", nullable = false)
    private Integer dureePret = 14;

    @OneToMany(mappedBy = "adherent")
    private List<PretLivre> prets = new ArrayList<>();

    @OneToMany(mappedBy = "adherent")
    private List<Reservation> reservations = new ArrayList<>();

    @Transient
    private boolean penalise = false;

    // Getters et Setters
    public Long getIdAdherent() {
        return idAdherent;
    }

    public void setIdAdherent(Long idAdherent) {
        this.idAdherent = idAdherent;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public TypeAdherent getTypeAdherent() {
        return typeAdherent;
    }

    public void setTypeAdherent(TypeAdherent typeAdherent) {
        this.typeAdherent = typeAdherent;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public LocalDate getDateInscription() {
        return dateInscription;
    }

    public void setDateInscription(LocalDate dateInscription) {
        this.dateInscription = dateInscription;
    }

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public Integer getMaxLivresDomicile() {
        return maxLivresDomicile;
    }

    public void setMaxLivresDomicile(Integer maxLivresDomicile) {
        this.maxLivresDomicile = maxLivresDomicile;
    }

    public Integer getMaxLivresSurplace() {
        return maxLivresSurplace;
    }

    public void setMaxLivresSurplace(Integer maxLivresSurplace) {
        this.maxLivresSurplace = maxLivresSurplace;
    }

    public Integer getLivresEmpruntesDomicile() {
        return livresEmpruntesDomicile;
    }

    public void setLivresEmpruntesDomicile(Integer livresEmpruntesDomicile) {
        this.livresEmpruntesDomicile = livresEmpruntesDomicile;
    }

    public Integer getLivresEmpruntesSurplace() {
        return livresEmpruntesSurplace;
    }

    public void setLivresEmpruntesSurplace(Integer livresEmpruntesSurplace) {
        this.livresEmpruntesSurplace = livresEmpruntesSurplace;
    }

    public Integer getDureePret() {
        return dureePret;
    }

    public void setDureePret(Integer dureePret) {
        this.dureePret = dureePret;
    }

    public List<PretLivre> getPrets() {
        return prets;
    }

    public void setPrets(List<PretLivre> prets) {
        this.prets = prets;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    public boolean isPenalise() {
        return penalise;
    }

    public void setPenalise(boolean penalise) {
        this.penalise = penalise;
    }

    @PrePersist
    protected void onCreate() {
        if (dateInscription == null) {
            dateInscription = LocalDate.now();
        }
    }

    // Méthodes utilitaires
    public boolean peutEmprunterDomicile() {
        return livresEmpruntesDomicile < maxLivresDomicile;
    }

    public boolean peutEmprunterSurPlace() {
        return livresEmpruntesSurplace < maxLivresSurplace;
    }

    public int getLivresRestantsDomicile() {
        return maxLivresDomicile - livresEmpruntesDomicile;
    }

    public int getLivresRestantsSurPlace() {
        return maxLivresSurplace - livresEmpruntesSurplace;
    }

    public int getAge() {
        return LocalDate.now().getYear() - dateNaissance.getYear();
    }
} 