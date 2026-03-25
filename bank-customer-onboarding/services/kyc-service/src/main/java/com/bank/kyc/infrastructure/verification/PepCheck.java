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
public class PepCheck {

    private static final Set<String> MOCK_PEP_LIST = Set.of(
            "PRESIDENT EXAMPLE",
            "SENATOR TESTCASE",
            "MINISTER DEMO"
    );

    public KycCheck perform(KycRequestEvent event) {
        log.info("Performing PEP check for customer: {}", event.getCustomerId());

        String fullName = (event.getFirstName() + " " + event.getLastName()).toUpperCase();
        boolean isPep = MOCK_PEP_LIST.stream()
                .anyMatch(pepName -> fullName.contains(pepName));

        int riskScore = isPep ? 60 : 5;

        return KycCheck.builder()
                .customerId(event.getCustomerId())
                .checkType(CheckType.PEP_CHECK)
                .status(isPep ? CheckStatus.FAILED : CheckStatus.PASSED)
                .details(isPep ? "Customer identified as Politically Exposed Person"
                        : "No PEP match found")
                .riskScore(riskScore)
                .build();
    }
}
