package com.bank.kyc.infrastructure.verification;

import com.bank.kyc.core.event.KycRequestEvent;
import com.bank.kyc.core.model.CheckStatus;
import com.bank.kyc.core.model.CheckType;
import com.bank.kyc.core.model.KycCheck;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;

@Component
@Slf4j
public class IdentityVerificationCheck {

    public KycCheck perform(KycRequestEvent event) {
        log.info("Performing identity verification for customer: {}", event.getCustomerId());

        boolean isValid = event.getFirstName() != null && !event.getFirstName().isBlank()
                && event.getLastName() != null && !event.getLastName().isBlank()
                && event.getDateOfBirth() != null
                && Period.between(event.getDateOfBirth(), LocalDate.now()).getYears() >= 18;

        int riskScore = isValid ? 10 : 80;

        return KycCheck.builder()
                .customerId(event.getCustomerId())
                .checkType(CheckType.IDENTITY_VERIFICATION)
                .status(isValid ? CheckStatus.PASSED : CheckStatus.FAILED)
                .details(isValid ? "Identity verified successfully" : "Identity verification failed - invalid personal details or underage")
                .riskScore(riskScore)
                .build();
    }
}
