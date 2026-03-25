package com.bank.notification.application;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {

    private UUID id;
    private UUID customerId;
    private String type;
    private String recipientEmail;
    private String subject;
    private String message;
    private String status;
    private LocalDateTime sentAt;
}
