package com.bank.kyc.application;

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
public class KycCheckResponse {

    private UUID id;
    private UUID customerId;
    private String checkType;
    private String status;
    private String details;
    private int riskScore;
    private LocalDateTime performedAt;
}
