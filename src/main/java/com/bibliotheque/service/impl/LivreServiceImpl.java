package com.bibliotheque.service.impl;

import com.bibliotheque.model.Livre;
import com.bibliotheque.repository.LivreRepository;
import com.bibliotheque.service.LivreService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LivreServiceImpl implements LivreService {

    private final LivreRepository livreRepository;

    @Autowired
    public LivreServiceImpl(LivreRepository livreRepository) {
        this.livreRepository = livreRepository;
    }

    @Override
    public Livre saveLivre(Livre livre) {
        return livreRepository.save(livre);
    }

    @Override
    public Livre saveLivreWithImage(Livre livre, MultipartFile image) throws IOException {
        if (image != null && !image.isEmpty()) {
            livre.setImage(
                image.getOriginalFilename(),
                image.getContentType(),
                image.getBytes()
            );
        }
        return livreRepository.save(livre);
    }

    @Override
    public Optional<Livre> getLivreById(Long id) {
        return livreRepository.findById(id);
    }

    @Override
    public Optional<Livre> getLivreByIsbn(String isbn) {
        return livreRepository.findByIsbn(isbn);
    }

    @Override
    public List<Livre> getAllLivres() {
        return livreRepository.findAll();
    }

    @Override
    public List<Livre> getAllLivresWithExemplaires() {
        return livreRepository.findAllWithExemplaires();
    }

    @Override
    public List<Livre> getLivresByTitre(String titre) {
        return livreRepository.findByTitreContainingIgnoreCase(titre);
    }

    @Override
    public List<Livre> getLivresByAuteur(String auteur) {
        return livreRepository.findByAuteurContainingIgnoreCase(auteur);
    }

    @Override
    public List<Livre> getLivresDisponibles() {
        return livreRepository.findAllAvailable();
    }

    @Override
    public List<Livre> getLivresByAgeMinimum(int age) {
        return livreRepository.findAllByAgeMinimum(age);
    }

    @Override
    public List<Livre> getLivresSansExemplaires() {
        return livreRepository.findAllWithoutExemplaires();
    }

    @Override
    public void deleteLivre(Long id) {
        livreRepository.deleteById(id);
    }

    @Override
    public boolean existsByIsbn(String isbn) {
        return livreRepository.existsByIsbn(isbn);
    }

    @Override
    @Transactional
    public void updateImage(Long livreId, MultipartFile image) throws IOException {
        Livre livre = livreRepository.findById(livreId)
            .orElseThrow(() -> new EntityNotFoundException("Livre non trouvé avec l'ID: " + livreId));

        if (image != null && !image.isEmpty()) {
            livre.setImage(
                image.getOriginalFilename(),
                image.getContentType(),
                image.getBytes()
            );
            livreRepository.save(livre);
        }
    }

    @Override
    @Transactional
    public void deleteImage(Long livreId) {
        Livre livre = livreRepository.findById(livreId)
            .orElseThrow(() -> new EntityNotFoundException("Livre non trouvé avec l'ID: " + livreId));

        livre.supprimerImage();
        livreRepository.save(livre);
    }

    @Override
    public byte[] getImageData(Long livreId) {
        return livreRepository.findById(livreId)
            .map(Livre::getImageDonnees)
            .orElseThrow(() -> new EntityNotFoundException("Livre non trouvé avec l'ID: " + livreId));
    }
} 