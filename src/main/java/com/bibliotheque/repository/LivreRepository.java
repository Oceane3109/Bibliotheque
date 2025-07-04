package com.bibliotheque.repository;

import com.bibliotheque.model.Livre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LivreRepository extends JpaRepository<Livre, Long> {
    Optional<Livre> findByIsbn(String isbn);
    List<Livre> findByTitreContainingIgnoreCase(String titre);
    List<Livre> findByAuteurContainingIgnoreCase(String auteur);
    boolean existsByIsbn(String isbn);
    
    @Query("SELECT l FROM Livre l WHERE l.ageMinimum <= :age")
    List<Livre> findAllByAgeMinimum(int age);
    
    @Query("SELECT l FROM Livre l WHERE EXISTS (SELECT e FROM Exemplaire e WHERE e.livre = l AND e.etat = 'disponible')")
    List<Livre> findAllAvailable();
    
    @Query("SELECT l FROM Livre l LEFT JOIN l.exemplaires e GROUP BY l HAVING COUNT(e) = 0")
    List<Livre> findAllWithoutExemplaires();
    
    @Query("SELECT DISTINCT l FROM Livre l LEFT JOIN FETCH l.exemplaires")
    List<Livre> findAllWithExemplaires();
} 