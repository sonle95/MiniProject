package likeUniquloWeb.controller;

import likeUniquloWeb.dto.response.OrderNotificationResponse;
import likeUniquloWeb.service.OrderNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
public class OrderNotificationController {

    private final OrderNotificationService notificationService;

    @PostMapping("/webhook/subscribe")
    public ResponseEntity<?> subscribeWebhook(
            @RequestParam String clientId,
            @RequestParam String webhookUrl) {

        try {
            notificationService.subscribeWebhook(clientId, webhookUrl);

            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Webhook subscribed successfully");
            response.put("clientId", clientId);
            response.put("webhookUrl", webhookUrl);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/webhook/unsubscribe")
    public ResponseEntity<?> unsubscribeWebhook(
            @RequestParam String clientId) {

        notificationService.unsubscribeWebhook(clientId);

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Webhook unsubscribed successfully");

        return ResponseEntity.ok(response);
    }


    @GetMapping
    public ResponseEntity<Map<String, Object>> getRecentNotifications(
            @RequestParam(defaultValue = "10") int limit) {

        List<OrderNotificationResponse> notifications = notificationService.getRecentNotifications(limit);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("count", notifications.size());
        response.put("notifications", notifications);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/since")
    public ResponseEntity<Map<String, Object>> getNotificationsSince(
            @RequestParam String since) {
                LocalDateTime sinceTime = LocalDateTime.parse(since);
                List<OrderNotificationResponse> notifications = notificationService.getNotificationsSince(sinceTime);

                Map<String, Object> response = new HashMap<>();
                response.put("status", "success");
                response.put("count", notifications.size());
                response.put("notifications", notifications);
                return ResponseEntity.ok(response);


    }

}
