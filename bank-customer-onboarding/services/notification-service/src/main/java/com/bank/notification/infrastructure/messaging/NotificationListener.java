package com.bank.notification.infrastructure.messaging;

import com.bank.notification.application.NotificationService;
import com.bank.notification.core.event.NotificationEvent;
import com.bank.notification.infrastructure.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationListener {

    private final NotificationService notificationService;

    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    public void handleNotification(NotificationEvent event) {
        log.info("Received notification event for customer: {}", event.getCustomerId());
        try {
            notificationService.processNotification(event);
        } catch (Exception e) {
            log.error("Error processing notification for customer: {}", event.getCustomerId(), e);
        }
    }
}
