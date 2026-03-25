package com.bank.kyc.application;

import com.bank.kyc.core.event.KycRequestEvent;
import com.bank.kyc.core.event.KycResultEvent;
import com.bank.kyc.core.event.NotificationEvent;
import com.bank.kyc.core.model.CheckStatus;
import com.bank.kyc.core.model.KycCheck;
import com.bank.kyc.core.repository.KycCheckRepository;
import com.bank.kyc.infrastructure.messaging.RabbitMQPublisher;
import com.bank.kyc.infrastructure.verification.DocumentValidationCheck;
import com.bank.kyc.infrastructure.verification.IdentityVerificationCheck;
import com.bank.kyc.infrastructure.verification.PepCheck;
import com.bank.kyc.infrastructure.verification.SanctionsScreeningCheck;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class KycService {

    private final KycCheckRepository kycCheckRepository;
    private final IdentityVerificationCheck identityVerificationCheck;
    private final DocumentValidationCheck documentValidationCheck;
    private final SanctionsScreeningCheck sanctionsScreeningCheck;
    private final PepCheck pepCheck;
    private final RabbitMQPublisher rabbitMQPublisher;

    @Transactional
    public void processKycRequest(KycRequestEvent event) {
        log.info("Processing KYC request for customer: {}", event.getCustomerId());

        KycCheck identityCheck = identityVerificationCheck.perform(event);
        KycCheck documentCheck = documentValidationCheck.perform(event);
        KycCheck sanctionsCheck = sanctionsScreeningCheck.perform(event);
        KycCheck pepCheckResult = pepCheck.perform(event);

        kycCheckRepository.save(identityCheck);
        kycCheckRepository.save(documentCheck);
        kycCheckRepository.save(sanctionsCheck);
        kycCheckRepository.save(pepCheckResult);

        List<KycCheck> allChecks = List.of(identityCheck, documentCheck, sanctionsCheck, pepCheckResult);

        boolean allPassed = allChecks.stream().allMatch(c -> c.getStatus() == CheckStatus.PASSED);
        int aggregateRiskScore = (int) allChecks.stream().mapToInt(KycCheck::getRiskScore).average().orElse(50);

        String kycStatus = allPassed && aggregateRiskScore < 50 ? "KYC_APPROVED" : "KYC_REJECTED";

        KycResultEvent resultEvent = KycResultEvent.builder()
                .customerId(event.getCustomerId())
                .status(kycStatus)
                .riskScore(aggregateRiskScore)
                .details("KYC verification completed. Risk score: " + aggregateRiskScore)
                .build();

        rabbitMQPublisher.publishKycResult(resultEvent);

        NotificationEvent notificationEvent = NotificationEvent.builder()
                .customerId(event.getCustomerId())
                .type(kycStatus)
                .email(event.getEmail())
                .subject("KYC Verification " + ("KYC_APPROVED".equals(kycStatus) ? "Approved" : "Rejected"))
                .message("Dear " + event.getFirstName() + " " + event.getLastName()
                        + ", your KYC verification has been " + ("KYC_APPROVED".equals(kycStatus) ? "approved" : "rejected")
                        + ". Risk score: " + aggregateRiskScore)
                .build();

        rabbitMQPublisher.publishNotification(notificationEvent);

        log.info("KYC processing completed for customer: {}, status: {}, riskScore: {}",
                event.getCustomerId(), kycStatus, aggregateRiskScore);
    }

    @Transactional(readOnly = true)
    public KycStatusResponse getKycStatus(UUID customerId) {
        List<KycCheck> checks = kycCheckRepository.findByCustomerId(customerId);
        if (checks.isEmpty()) {
            return KycStatusResponse.builder()
                    .customerId(customerId)
                    .overallStatus("NOT_STARTED")
                    .riskScore(0)
                    .build();
        }

        boolean allPassed = checks.stream().allMatch(c -> c.getStatus() == CheckStatus.PASSED);
        boolean anyFailed = checks.stream().anyMatch(c -> c.getStatus() == CheckStatus.FAILED);
        int avgScore = (int) checks.stream().mapToInt(KycCheck::getRiskScore).average().orElse(0);

        String status;
        if (anyFailed) {
            status = "FAILED";
        } else if (allPassed) {
            status = "APPROVED";
        } else {
            status = "IN_PROGRESS";
        }

        return KycStatusResponse.builder()
                .customerId(customerId)
                .overallStatus(status)
                .riskScore(avgScore)
                .totalChecks(checks.size())
                .passedChecks((int) checks.stream().filter(c -> c.getStatus() == CheckStatus.PASSED).count())
                .failedChecks((int) checks.stream().filter(c -> c.getStatus() == CheckStatus.FAILED).count())
                .build();
    }

    @Transactional(readOnly = true)
    public List<KycCheckResponse> getKycChecks(UUID customerId) {
        return kycCheckRepository.findByCustomerId(customerId).stream()
                .map(check -> KycCheckResponse.builder()
                        .id(check.getId())
                        .customerId(check.getCustomerId())
                        .checkType(check.getCheckType().name())
                        .status(check.getStatus().name())
                        .details(check.getDetails())
                        .riskScore(check.getRiskScore())
                        .performedAt(check.getPerformedAt())
                        .build())
                .toList();
    }

    @Transactional
    public void retryKyc(UUID customerId, KycRequestEvent event) {
        log.info("Retrying KYC for customer: {}", customerId);
        kycCheckRepository.deleteByCustomerId(customerId);
        processKycRequest(event);
    }
}
