package com.bank.kyc.infrastructure.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String KYC_REQUEST_QUEUE = "kyc.request.queue";
    public static final String KYC_RESULT_QUEUE = "kyc.result.queue";
    public static final String NOTIFICATION_QUEUE = "notification.queue";
    public static final String EXCHANGE = "bank.exchange";

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue kycRequestQueue() {
        return QueueBuilder.durable(KYC_REQUEST_QUEUE).build();
    }

    @Bean
    public Queue kycResultQueue() {
        return QueueBuilder.durable(KYC_RESULT_QUEUE).build();
    }

    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(NOTIFICATION_QUEUE).build();
    }

    @Bean
    public Binding kycRequestBinding(Queue kycRequestQueue, TopicExchange exchange) {
        return BindingBuilder.bind(kycRequestQueue).to(exchange).with("kyc.request");
    }

    @Bean
    public Binding kycResultBinding(Queue kycResultQueue, TopicExchange exchange) {
        return BindingBuilder.bind(kycResultQueue).to(exchange).with("kyc.result");
    }

    @Bean
    public Binding notificationBinding(Queue notificationQueue, TopicExchange exchange) {
        return BindingBuilder.bind(notificationQueue).to(exchange).with("notification");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
