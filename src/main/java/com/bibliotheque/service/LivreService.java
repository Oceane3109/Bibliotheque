package com.bibliotheque.service;

import com.bibliotheque.model.Livre;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface LivreService {
    Livre saveLivre(Livre livre);
    Optional<Livre> getLivreById(Long id);
    Optional<Livre> getLivreByIsbn(String isbn);
    List<Livre> getAllLivres();
    List<Livre> getLivresByTitre(String titre);
    List<Livre> getLivresByAuteur(String auteur);
    List<Livre> getLivresDisponibles();
    List<Livre> getLivresByAgeMinimum(int age);
    List<Livre> getLivresSansExemplaires();
    void deleteLivre(Long id);
    boolean existsByIsbn(String isbn);
    
    List<Livre> getAllLivresWithExemplaires();

    Optional<Livre> getLivreWithExemplairesById(Long id);
} 