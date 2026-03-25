package com.bank.kyc;

import com.bank.kyc.core.event.KycRequestEvent;
import com.bank.kyc.core.model.CheckStatus;
import com.bank.kyc.core.model.CheckType;
import com.bank.kyc.core.model.KycCheck;
import com.bank.kyc.infrastructure.verification.DocumentValidationCheck;
import com.bank.kyc.infrastructure.verification.IdentityVerificationCheck;
import com.bank.kyc.infrastructure.verification.PepCheck;
import com.bank.kyc.infrastructure.verification.SanctionsScreeningCheck;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class KycVerificationUnitTest {

    @Test
    void identityVerification_validCustomer_shouldPass() {
        IdentityVerificationCheck check = new IdentityVerificationCheck();
        KycRequestEvent event = KycRequestEvent.builder()
                .customerId(UUID.randomUUID())
                .firstName("John")
                .lastName("Doe")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .build();

        KycCheck result = check.perform(event);

        assertEquals(CheckStatus.PASSED, result.getStatus());
        assertEquals(CheckType.IDENTITY_VERIFICATION, result.getCheckType());
        assertTrue(result.getRiskScore() < 50);
    }

    @Test
    void identityVerification_underage_shouldFail() {
        IdentityVerificationCheck check = new IdentityVerificationCheck();
        KycRequestEvent event = KycRequestEvent.builder()
                .customerId(UUID.randomUUID())
                .firstName("John")
                .lastName("Doe")
                .dateOfBirth(LocalDate.now().minusYears(15))
                .build();

        KycCheck result = check.perform(event);

        assertEquals(CheckStatus.FAILED, result.getStatus());
    }

    @Test
    void documentValidation_validPassport_shouldPass() {
        DocumentValidationCheck check = new DocumentValidationCheck();
        KycRequestEvent event = KycRequestEvent.builder()
                .customerId(UUID.randomUUID())
                .idDocumentType("PASSPORT")
                .idDocumentNumber("AB1234567")
                .build();

        KycCheck result = check.perform(event);

        assertEquals(CheckStatus.PASSED, result.getStatus());
        assertEquals(CheckType.DOCUMENT_VALIDATION, result.getCheckType());
    }

    @Test
    void sanctionsScreening_cleanCustomer_shouldPass() {
        SanctionsScreeningCheck check = new SanctionsScreeningCheck();
        KycRequestEvent event = KycRequestEvent.builder()
                .customerId(UUID.randomUUID())
                .firstName("Regular")
                .lastName("Customer")
                .build();

        KycCheck result = check.perform(event);

        assertEquals(CheckStatus.PASSED, result.getStatus());
        assertEquals(CheckType.SANCTIONS_SCREENING, result.getCheckType());
    }

    @Test
    void pepCheck_cleanCustomer_shouldPass() {
        PepCheck check = new PepCheck();
        KycRequestEvent event = KycRequestEvent.builder()
                .customerId(UUID.randomUUID())
                .firstName("Regular")
                .lastName("Customer")
                .build();

        KycCheck result = check.perform(event);

        assertEquals(CheckStatus.PASSED, result.getStatus());
        assertEquals(CheckType.PEP_CHECK, result.getCheckType());
    }
}
