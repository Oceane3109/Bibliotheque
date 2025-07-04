package com.bibliotheque.service;

import com.bibliotheque.model.NoteLivre;
import com.bibliotheque.model.Livre;
import com.bibliotheque.model.Adherent;
import java.util.List;
import java.util.Optional;

public interface NoteLivreService {
    NoteLivre saveNote(NoteLivre noteLivre);
    Optional<NoteLivre> getNoteByLivreAndAdherent(Livre livre, Adherent adherent);
    List<NoteLivre> getNotesByLivre(Livre livre);
    List<NoteLivre> getNotesByAdherent(Adherent adherent);
    Double getAverageNoteByLivre(Livre livre);
    List<Object[]> getTopLivresByNote();
    Long countByNote(int note);
    Long countAllNotes();
} 