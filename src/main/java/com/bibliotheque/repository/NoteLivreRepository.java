package com.bibliotheque.repository;

import com.bibliotheque.model.NoteLivre;
import com.bibliotheque.model.Livre;
import com.bibliotheque.model.Adherent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface NoteLivreRepository extends JpaRepository<NoteLivre, Long> {
    List<NoteLivre> findByLivre(Livre livre);
    List<NoteLivre> findByAdherent(Adherent adherent);
    Optional<NoteLivre> findByLivreAndAdherent(Livre livre, Adherent adherent);

    @Query("SELECT AVG(n.note) FROM NoteLivre n WHERE n.livre = :livre")
    Double findAverageNoteByLivre(Livre livre);

    @Query("SELECT n.livre, AVG(n.note) as avgNote, COUNT(n) as nbAvis FROM NoteLivre n GROUP BY n.livre ORDER BY avgNote DESC, nbAvis DESC")
    List<Object[]> findTopLivresByNote();

    @Query("SELECT COUNT(n) FROM NoteLivre n WHERE n.note = :note")
    Long countByNote(int note);
} 