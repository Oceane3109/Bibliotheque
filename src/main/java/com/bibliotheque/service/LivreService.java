package com.bibliotheque.service;

import com.bibliotheque.model.Livre;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface LivreService {
    Livre saveLivre(Livre livre);
    Livre saveLivreWithImage(Livre livre, MultipartFile image) throws IOException;
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
    
    // Méthodes de gestion des images
    void updateImage(Long livreId, MultipartFile image) throws IOException;
    void deleteImage(Long livreId);
    byte[] getImageData(Long livreId);

    List<Livre> getAllLivresWithExemplaires();

    Optional<Livre> getLivreWithExemplairesById(Long id);
} 