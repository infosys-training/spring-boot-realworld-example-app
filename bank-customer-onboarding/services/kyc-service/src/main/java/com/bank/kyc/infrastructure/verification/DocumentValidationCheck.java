package com.bank.kyc.infrastructure.verification;

import com.bank.kyc.core.event.KycRequestEvent;
import com.bank.kyc.core.model.CheckStatus;
import com.bank.kyc.core.model.CheckType;
import com.bank.kyc.core.model.KycCheck;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
@Slf4j
public class DocumentValidationCheck {

    private static final Pattern PASSPORT_PATTERN = Pattern.compile("^[A-Z]{1,2}[0-9]{6,9}$");
    private static final Pattern DRIVERS_LICENSE_PATTERN = Pattern.compile("^[A-Z0-9]{5,20}$");
    private static final Pattern NATIONAL_ID_PATTERN = Pattern.compile("^[A-Z0-9]{5,20}$");

    public KycCheck perform(KycRequestEvent event) {
        log.info("Performing document validation for customer: {}", event.getCustomerId());

        boolean isValid = validateDocument(event.getIdDocumentType(), event.getIdDocumentNumber());
        int riskScore = isValid ? 5 : 70;

        return KycCheck.builder()
                .customerId(event.getCustomerId())
                .checkType(CheckType.DOCUMENT_VALIDATION)
                .status(isValid ? CheckStatus.PASSED : CheckStatus.FAILED)
                .details(isValid ? "Document format validated successfully"
                        : "Document validation failed - invalid format for " + event.getIdDocumentType())
                .riskScore(riskScore)
                .build();
    }

    private boolean validateDocument(String documentType, String documentNumber) {
        if (documentType == null || documentNumber == null) return false;

        return switch (documentType) {
            case "PASSPORT" -> PASSPORT_PATTERN.matcher(documentNumber).matches();
            case "DRIVERS_LICENSE" -> DRIVERS_LICENSE_PATTERN.matcher(documentNumber).matches();
            case "NATIONAL_ID" -> NATIONAL_ID_PATTERN.matcher(documentNumber).matches();
            default -> false;
        };
    }
}
