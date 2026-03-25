package com.bank.kyc.api;

import com.bank.kyc.application.KycCheckResponse;
import com.bank.kyc.application.KycService;
import com.bank.kyc.application.KycStatusResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/kyc")
@RequiredArgsConstructor
@Tag(name = "KYC", description = "KYC verification API")
public class KycController {

    private final KycService kycService;

    @GetMapping("/{customerId}")
    @Operation(summary = "Get KYC status for a customer")
    public ResponseEntity<KycStatusResponse> getKycStatus(@PathVariable UUID customerId) {
        return ResponseEntity.ok(kycService.getKycStatus(customerId));
    }

    @GetMapping("/{customerId}/checks")
    @Operation(summary = "Get individual KYC check results")
    public ResponseEntity<List<KycCheckResponse>> getKycChecks(@PathVariable UUID customerId) {
        return ResponseEntity.ok(kycService.getKycChecks(customerId));
    }

    @PostMapping("/{customerId}/retry")
    @Operation(summary = "Retry failed KYC verification")
    public ResponseEntity<String> retryKyc(@PathVariable UUID customerId) {
        return ResponseEntity.ok("KYC retry initiated for customer: " + customerId);
    }
}
