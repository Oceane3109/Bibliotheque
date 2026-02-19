package com.bibliotheque.service;

import com.bibliotheque.model.Exemplaire;
import com.bibliotheque.model.Livre;

import java.util.List;
import java.util.Optional;

public interface ExemplaireService {
    Exemplaire saveExemplaire(Exemplaire exemplaire);
    Optional<Exemplaire> getExemplaireById(Long id);
    Optional<Exemplaire> getExemplaireByCode(String codeExemplaire);
    List<Exemplaire> getAllExemplaires();
    List<Exemplaire> getExemplairesByLivre(Livre livre);
    List<Exemplaire> getExemplairesByEtat(String etat);
    List<Exemplaire> getExemplairesByLivreAndEtat(Livre livre, String etat);
    List<Exemplaire> getExemplairesDisponiblesByLivre(Livre livre);
    void deleteExemplaire(Long id);
    boolean existsByCodeExemplaire(String codeExemplaire);
} 