package com.bibliotheque.service.impl;

import com.bibliotheque.model.Livre;
import com.bibliotheque.repository.LivreRepository;
import com.bibliotheque.service.LivreService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    // Je supprime toute la logique liée à l'image binaire (MultipartFile image, setImage, etc.)
    // Je ne garde que la gestion du champ imageUrl

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
    public Optional<Livre> getLivreWithExemplairesById(Long id) {
        return livreRepository.findByIdWithExemplaires(id);
    }
} 