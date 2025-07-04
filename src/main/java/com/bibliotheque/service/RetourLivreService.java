package com.bibliotheque.service;

import com.bibliotheque.model.RetourLivre;
import com.bibliotheque.model.PretLivre;
import java.util.List;
import java.util.Optional;

public interface RetourLivreService {
    RetourLivre saveRetour(RetourLivre retourLivre);
    Optional<RetourLivre> getRetourById(Long id);
    List<RetourLivre> getAllRetours();
    List<RetourLivre> getRetoursByPret(PretLivre pretLivre);
    Optional<RetourLivre> getDernierRetourPourPret(PretLivre pretLivre);
    void deleteRetour(Long id);
} 