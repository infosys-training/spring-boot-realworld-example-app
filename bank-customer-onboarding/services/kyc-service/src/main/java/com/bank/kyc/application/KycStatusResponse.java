package com.bank.kyc.application;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KycStatusResponse {

    private UUID customerId;
    private String overallStatus;
    private int riskScore;
    private int totalChecks;
    private int passedChecks;
    private int failedChecks;
}
