package com.bank.kyc.core.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KycResultEvent implements Serializable {

    private UUID customerId;
    private String status;
    private int riskScore;
    private String details;
}
