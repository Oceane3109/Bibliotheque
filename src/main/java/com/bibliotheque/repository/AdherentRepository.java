package com.bibliotheque.repository;

import com.bibliotheque.model.Adherent;
import com.bibliotheque.model.TypeAdherent;
import com.bibliotheque.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdherentRepository extends JpaRepository<Adherent, Long> {
    Optional<Adherent> findByUser(User user);
    Optional<Adherent> findByEmail(String email);
    List<Adherent> findByTypeAdherent(TypeAdherent typeAdherent);
    boolean existsByEmail(String email);
    boolean existsByUser(User user);
    
    @Query("SELECT a FROM Adherent a WHERE a.livresEmpruntesDomicile > 0 OR a.livresEmpruntesSurplace > 0")
    List<Adherent> findAllWithActivePrets();

    @Query("SELECT a FROM Adherent a LEFT JOIN FETCH a.prets WHERE a.user.nomUtilisateur = :username")
    Optional<Adherent> findByUserUsernameWithPrets(String username);

    @Query("SELECT DISTINCT a FROM Adherent a LEFT JOIN FETCH a.prets WHERE a.idAdherent = :id")
    Optional<Adherent> findByIdWithPrets(Long id);

    @Query("SELECT COUNT(p2) FROM PretLivre p JOIN p.prolongements p2 WHERE p.adherent.idAdherent = :adherentId")
    int countProlongementsByAdherentId(@Param("adherentId") Long adherentId);

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.adherent.idAdherent = :adherentId")
    int countReservationsByAdherentId(@Param("adherentId") Long adherentId);
} 