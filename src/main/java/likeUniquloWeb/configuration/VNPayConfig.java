package likeUniquloWeb.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class VNPayConfig {

    @Value("${vnpay.tmn-code}")
    private String tmnCode; // Mã website của bạn tại VNPay

    @Value("${vnpay.hash-secret}")
    private String hashSecret; // Secret key để mã hóa

    @Value("${vnpay.url}")
    private String vnpUrl; // URL thanh toán VNPay

    @Value("${vnpay.return-url}")
    private String returnUrl; // URL callback sau khi thanh toán

    @Value("${vnpay.version}")
    private String version = "2.1.0";

    @Value("${vnpay.command}")
    private String command = "pay";

    @Value("${vnpay.order-type}")
    private String orderType = "other";
}
