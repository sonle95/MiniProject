package likeUniquloWeb.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderNotificationResponse {
    Long orderId;
    String orderNumber;
    String customerName;
    String customerEmail;
    BigDecimal totalAmount;
    String orderStatus;

    @JsonFormat
    LocalDateTime createdAt;
    int itemCount;

    String message;
    public String getMessage() {
        if (message == null && orderNumber != null) {
            return String.format("Đơn hàng mới #%s từ %s", orderNumber, customerName);
        }
        return message;
    }
}
