package com.bibliotheque.service;

import com.bibliotheque.model.Notification;
import com.bibliotheque.model.Adherent;
import java.util.List;
import java.util.Optional;

public interface NotificationService {
    Notification saveNotification(Notification notification);
    Optional<Notification> getNotificationById(Long id);
    List<Notification> getAllNotifications();
    List<Notification> getNotificationsByAdherent(Adherent adherent);
    List<Notification> getNotificationsNonLuesByAdherent(Adherent adherent);
    void deleteNotification(Long id);
    void marquerCommeLue(Long id);
} 