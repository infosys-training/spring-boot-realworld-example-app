package com.bank.notification.api;

import com.bank.notification.application.NotificationResponse;
import com.bank.notification.application.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Notification API")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/{customerId}")
    @Operation(summary = "Get notifications for a customer")
    public ResponseEntity<List<NotificationResponse>> getNotifications(@PathVariable UUID customerId) {
        return ResponseEntity.ok(notificationService.getNotifications(customerId));
    }
}
