package com.bank.notification.core.repository;

import com.bank.notification.core.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    List<Notification> findByCustomerIdOrderBySentAtDesc(UUID customerId);
}
