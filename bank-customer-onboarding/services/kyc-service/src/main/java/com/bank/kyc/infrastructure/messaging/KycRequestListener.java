package com.bank.kyc.infrastructure.messaging;

import com.bank.kyc.application.KycService;
import com.bank.kyc.core.event.KycRequestEvent;
import com.bank.kyc.infrastructure.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KycRequestListener {

    private final KycService kycService;

    @RabbitListener(queues = RabbitMQConfig.KYC_REQUEST_QUEUE)
    public void handleKycRequest(KycRequestEvent event) {
        log.info("Received KYC request event for customer: {}", event.getCustomerId());
        try {
            kycService.processKycRequest(event);
        } catch (Exception e) {
            log.error("Error processing KYC request for customer: {}", event.getCustomerId(), e);
        }
    }
}
