package com.bank.kyc.infrastructure.verification;

import com.bank.kyc.core.event.KycRequestEvent;
import com.bank.kyc.core.model.CheckStatus;
import com.bank.kyc.core.model.CheckType;
import com.bank.kyc.core.model.KycCheck;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Slf4j
public class SanctionsScreeningCheck {

    private static final Set<String> MOCK_SANCTIONS_LIST = Set.of(
            "JOHN SANCTIONED",
            "JANE BLOCKED",
            "TERROR FINANCE",
            "MONEY LAUNDER"
    );

    public KycCheck perform(KycRequestEvent event) {
        log.info("Performing sanctions screening for customer: {}", event.getCustomerId());

        String fullName = (event.getFirstName() + " " + event.getLastName()).toUpperCase();
        boolean isSanctioned = MOCK_SANCTIONS_LIST.stream()
                .anyMatch(sanctionedName -> fullName.contains(sanctionedName));

        int riskScore = isSanctioned ? 100 : 5;

        return KycCheck.builder()
                .customerId(event.getCustomerId())
                .checkType(CheckType.SANCTIONS_SCREENING)
                .status(isSanctioned ? CheckStatus.FAILED : CheckStatus.PASSED)
                .details(isSanctioned ? "Customer name matched sanctions list"
                        : "No sanctions match found")
                .riskScore(riskScore)
                .build();
    }
}
