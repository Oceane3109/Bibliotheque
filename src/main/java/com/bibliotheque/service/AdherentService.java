package com.bibliotheque.service;

import com.bibliotheque.model.Adherent;
import com.bibliotheque.model.TypeAdherent;
import com.bibliotheque.model.User;

import java.util.List;
import java.util.Optional;

public interface AdherentService {
    Adherent saveAdherent(Adherent adherent);
    Optional<Adherent> getAdherentById(Long id);
    Optional<Adherent> getAdherentByUser(User user);
    Optional<Adherent> getAdherentByEmail(String email);
    List<Adherent> getAllAdherents();
    List<Adherent> getAdherentsByType(TypeAdherent typeAdherent);
    List<Adherent> getAdherentsWithActivePrets();
    void deleteAdherent(Long id);
    boolean existsByEmail(String email);
    boolean existsByUser(User user);
    
    // Méthodes de gestion des emprunts
    boolean peutEmprunterDomicile(Long adherentId);
    boolean peutEmprunterSurPlace(Long adherentId);
    void incrementerLivresEmpruntesDomicile(Long adherentId);
    void incrementerLivresEmpruntesSurPlace(Long adherentId);
    void decrementerLivresEmpruntesDomicile(Long adherentId);
    void decrementerLivresEmpruntesSurPlace(Long adherentId);
    boolean isPenalise(Long adherentId);

    Optional<Adherent> getAdherentByUserUsername(String username);
    Optional<Adherent> getAdherentByUserUsernameWithPrets(String username);
} 