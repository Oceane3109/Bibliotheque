package com.bibliotheque.service.impl;

import com.bibliotheque.model.Notification;
import com.bibliotheque.model.Adherent;
import com.bibliotheque.repository.NotificationRepository;
import com.bibliotheque.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;

    @Autowired
    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public Notification saveNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    @Override
    public Optional<Notification> getNotificationById(Long id) {
        return notificationRepository.findById(id);
    }

    @Override
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    @Override
    public List<Notification> getNotificationsByAdherent(Adherent adherent) {
        return notificationRepository.findByAdherent(adherent);
    }

    @Override
    public List<Notification> getNotificationsNonLuesByAdherent(Adherent adherent) {
        return notificationRepository.findByAdherentAndLuFalse(adherent);
    }

    @Override
    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }

    @Override
    public void marquerCommeLue(Long id) {
        notificationRepository.findById(id).ifPresent(n -> {
            n.setLu(true);
            notificationRepository.save(n);
        });
    }
} 