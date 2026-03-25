package com.bank.kyc.infrastructure.messaging;

import com.bank.kyc.core.event.KycResultEvent;
import com.bank.kyc.core.event.NotificationEvent;
import com.bank.kyc.infrastructure.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitMQPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishKycResult(KycResultEvent event) {
        log.info("Publishing KYC result event for customer: {}, status: {}", event.getCustomerId(), event.getStatus());
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, "kyc.result", event);
    }

    public void publishNotification(NotificationEvent event) {
        log.info("Publishing notification event for customer: {}", event.getCustomerId());
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, "notification", event);
    }
}
