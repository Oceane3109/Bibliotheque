package com.bibliotheque.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notifications")
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notification")
    private Long idNotification;

    @ManyToOne
    @JoinColumn(name = "id_adherent", nullable = false)
    private Adherent adherent;

    @NotBlank(message = "Le titre est obligatoire")
    @Column(name = "titre", nullable = false, length = 100)
    private String titre;

    @NotBlank(message = "Le message est obligatoire")
    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @NotNull(message = "La date d'envoi est obligatoire")
    @Column(name = "date_envoi", nullable = false)
    private LocalDateTime dateEnvoi;

    @Column(name = "lu", nullable = false)
    private Boolean lu = false;

    // Getters et Setters
    public Long getIdNotification() {
        return idNotification;
    }

    public void setIdNotification(Long idNotification) {
        this.idNotification = idNotification;
    }

    public Adherent getAdherent() {
        return adherent;
    }

    public void setAdherent(Adherent adherent) {
        this.adherent = adherent;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getDateEnvoi() {
        return dateEnvoi;
    }

    public void setDateEnvoi(LocalDateTime dateEnvoi) {
        this.dateEnvoi = dateEnvoi;
    }

    public Boolean getLu() {
        return lu;
    }

    public void setLu(Boolean lu) {
        this.lu = lu;
    }

    @PrePersist
    protected void onCreate() {
        if (dateEnvoi == null) {
            dateEnvoi = LocalDateTime.now();
        }
        if (lu == null) {
            lu = false;
        }
    }

    // Méthodes utilitaires
    public boolean isLue() {
        return lu != null && lu;
    }

    public boolean isNonLue() {
        return !isLue();
    }

    public String getResume() {
        if (message.length() <= 100) {
            return message;
        }
        return message.substring(0, 97) + "...";
    }
} 