package com.techblog.backend.service;

import com.techblog.backend.entity.Notification;
import com.techblog.backend.entity.NotificationType;
import com.techblog.backend.entity.User;
import com.techblog.backend.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public void createNotification(User user, String message, NotificationType type) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRead(false);

        notificationRepository.save(notification);

        messagingTemplate.convertAndSendToUser(
                user.getUsername(),
                "/queue/notifications",
                notification
        );
    }
}