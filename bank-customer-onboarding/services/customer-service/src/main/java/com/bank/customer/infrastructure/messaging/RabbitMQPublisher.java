package com.bank.customer.infrastructure.messaging;

import com.bank.customer.core.event.KycRequestEvent;
import com.bank.customer.infrastructure.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitMQPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishKycRequest(KycRequestEvent event) {
        log.info("Publishing KYC request event for customer: {}", event.getCustomerId());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                "kyc.request",
                event
        );
        log.info("KYC request event published successfully");
    }
}
