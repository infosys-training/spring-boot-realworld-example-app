package com.bank.notification.application;

import com.bank.notification.core.event.NotificationEvent;
import com.bank.notification.core.model.Notification;
import com.bank.notification.core.repository.NotificationRepository;
import com.bank.notification.infrastructure.mail.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    @Transactional
    public void processNotification(NotificationEvent event) {
        log.info("Processing notification for customer: {}, type: {}", event.getCustomerId(), event.getType());

        // Send email via MailHog
        emailService.sendEmail(event.getEmail(), event.getSubject(), event.getMessage());

        // Save notification to audit database
        Notification notification = Notification.builder()
                .customerId(event.getCustomerId())
                .type(event.getType())
                .recipientEmail(event.getEmail())
                .subject(event.getSubject())
                .message(event.getMessage())
                .status("SENT")
                .build();

        notificationRepository.save(notification);
        log.info("Notification saved and email sent for customer: {}", event.getCustomerId());
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotifications(UUID customerId) {
        return notificationRepository.findByCustomerIdOrderBySentAtDesc(customerId).stream()
                .map(n -> NotificationResponse.builder()
                        .id(n.getId())
                        .customerId(n.getCustomerId())
                        .type(n.getType())
                        .recipientEmail(n.getRecipientEmail())
                        .subject(n.getSubject())
                        .message(n.getMessage())
                        .status(n.getStatus())
                        .sentAt(n.getSentAt())
                        .build())
                .toList();
    }
}
