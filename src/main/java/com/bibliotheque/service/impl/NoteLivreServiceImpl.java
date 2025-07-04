package com.bibliotheque.service.impl;

import com.bibliotheque.model.NoteLivre;
import com.bibliotheque.model.Livre;
import com.bibliotheque.model.Adherent;
import com.bibliotheque.repository.NoteLivreRepository;
import com.bibliotheque.service.NoteLivreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class NoteLivreServiceImpl implements NoteLivreService {
    private final NoteLivreRepository noteLivreRepository;

    @Autowired
    public NoteLivreServiceImpl(NoteLivreRepository noteLivreRepository) {
        this.noteLivreRepository = noteLivreRepository;
    }

    @Override
    public NoteLivre saveNote(NoteLivre noteLivre) {
        return noteLivreRepository.save(noteLivre);
    }

    @Override
    public Optional<NoteLivre> getNoteByLivreAndAdherent(Livre livre, Adherent adherent) {
        return noteLivreRepository.findByLivreAndAdherent(livre, adherent);
    }

    @Override
    public List<NoteLivre> getNotesByLivre(Livre livre) {
        return noteLivreRepository.findByLivreWithAdherent(livre);
    }

    @Override
    public List<NoteLivre> getNotesByAdherent(Adherent adherent) {
        return noteLivreRepository.findByAdherent(adherent);
    }

    @Override
    public Double getAverageNoteByLivre(Livre livre) {
        return noteLivreRepository.findAverageNoteByLivre(livre);
    }

    @Override
    public List<Object[]> getTopLivresByNote() {
        return noteLivreRepository.findTopLivresByNote();
    }

    @Override
    public Long countByNote(int note) {
        return noteLivreRepository.countByNote(note);
    }

    @Override
    public Long countAllNotes() {
        return noteLivreRepository.count();
    }

    @Override
    public List<Object[]> getLastAvisWithDetails() {
        return noteLivreRepository.findLastAvisWithDetails();
    }
} 