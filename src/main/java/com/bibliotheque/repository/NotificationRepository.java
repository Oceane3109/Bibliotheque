package com.bibliotheque.repository;

import com.bibliotheque.model.Notification;
import com.bibliotheque.model.Adherent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByAdherent(Adherent adherent);
    List<Notification> findByAdherentAndLuFalse(Adherent adherent);
} 