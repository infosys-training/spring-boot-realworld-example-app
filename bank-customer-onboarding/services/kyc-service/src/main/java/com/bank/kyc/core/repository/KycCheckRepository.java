package com.bank.kyc.core.repository;

import com.bank.kyc.core.model.KycCheck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface KycCheckRepository extends JpaRepository<KycCheck, UUID> {

    List<KycCheck> findByCustomerId(UUID customerId);

    void deleteByCustomerId(UUID customerId);
}
