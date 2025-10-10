package likeUniquloWeb.service;

import likeUniquloWeb.dto.response.OrderNotificationResponse;
import likeUniquloWeb.entity.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderNotificationService {
    private final RestTemplate restTemplate;

    // Lưu trữ URL webhooks của các client đã đăng ký
    private final Map<String, String> webhookSubscribers = new ConcurrentHashMap<>();

    // Lưu trữ thông báo gần đây (cho polling)
    private final List<OrderNotificationResponse> recentNotifications = Collections.synchronizedList(
            new LinkedList<OrderNotificationResponse>() {
                @Override
                public boolean add(OrderNotificationResponse notification) {
                    // Giới hạn 100 thông báo gần đây
                    if (size() >= 100) {
                        remove(0);
                    }
                    return super.add(notification);
                }
            }
    );

    /**
     * Đăng ký webhook URL để nhận thông báo
     */
    public void subscribeWebhook(String clientId, String webhookUrl) {
        webhookSubscribers.put(clientId, webhookUrl);
        log.info("Webhook subscribed: {} -> {}", clientId, webhookUrl);
    }

    /**
     * Hủy đăng ký webhook
     */
    public void unsubscribeWebhook(String clientId) {
        webhookSubscribers.remove(clientId);
        log.info("Webhook unsubscribed: {}", clientId);
    }

    /**
     * Gửi thông báo khi có đơn hàng mới
     */
    public void notifyNewOrder(Order order) {
        OrderNotificationResponse notification = OrderNotificationResponse.builder()
                .orderId(order.getId())
                .orderNumber(order.getOrderNumber())
                .customerName(order.getUser().getUsername())
                .customerEmail(order.getUser().getEmail())
                .totalAmount(order.getTotalAmount())
                .orderStatus(order.getStatus().toString())
                .createdAt(LocalDateTime.now())
                .itemCount(order.getOrderItems().size())
                .message(String.format("Đơn hàng mới #%s từ %s",
                        order.getOrderNumber(),
                        order.getUser().getUsername()))
                .build();

        // Lưu vào danh sách gần đây
        recentNotifications.add(notification);

        // Gửi đến tất cả webhook subscribers
        webhookSubscribers.forEach((clientId, webhookUrl) -> {
            sendWebhookNotification(clientId, webhookUrl, notification);
        });

        log.info("Order notification sent for order: {}", order.getOrderNumber());
    }

    /**
     * Gửi HTTP POST tới webhook URL
     */
    private void sendWebhookNotification(String clientId, String webhookUrl,
                                         OrderNotificationResponse notification) {
        try {
            restTemplate.postForObject(webhookUrl, notification, String.class);
            log.info("Webhook notification sent to {}: {}", clientId, webhookUrl);
        } catch (Exception e) {
            log.error("Failed to send webhook to {}: {}", clientId, e.getMessage());
            // Có thể thêm retry logic hoặc lưu vào queue
        }
    }

    /**
     * Lấy thông báo gần đây (polling)
     */
    public List<OrderNotificationResponse> getRecentNotifications(int limit) {
        int fromIndex = Math.max(0, recentNotifications.size() - limit);
        return new ArrayList<>(recentNotifications.subList(fromIndex, recentNotifications.size()));
    }

    /**
     * Lấy thông báo theo thời gian
     */
    public List<OrderNotificationResponse> getNotificationsSince(LocalDateTime since) {
        return recentNotifications.stream()
                .filter(n -> n.getCreatedAt().isAfter(since))
                .toList();
    }
}
