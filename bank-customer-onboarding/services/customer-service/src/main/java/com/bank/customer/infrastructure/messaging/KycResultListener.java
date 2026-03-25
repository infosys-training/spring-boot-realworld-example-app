package com.bank.customer.infrastructure.messaging;

import com.bank.customer.application.CustomerService;
import com.bank.customer.core.event.KycResultEvent;
import com.bank.customer.infrastructure.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KycResultListener {

    private final CustomerService customerService;

    @RabbitListener(queues = RabbitMQConfig.KYC_RESULT_QUEUE)
    public void handleKycResult(KycResultEvent event) {
        log.info("Received KYC result event for customer: {}, status: {}", event.getCustomerId(), event.getStatus());
        try {
            customerService.handleKycResult(event);
        } catch (Exception e) {
            log.error("Error processing KYC result for customer: {}", event.getCustomerId(), e);
        }
    }
}
