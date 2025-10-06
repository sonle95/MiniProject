package likeUniquloWeb.service;

import likeUniquloWeb.dto.response.RevenueDTO;
import likeUniquloWeb.dto.response.TopProductDTO;
import likeUniquloWeb.dto.response.TopUserDTO;
import likeUniquloWeb.dto.response.YearlyRevenueDTO;
import likeUniquloWeb.enums.PaymentStatus;
import likeUniquloWeb.repository.OrderRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatisticService {
    OrderRepository orderRepository;

    public List<RevenueDTO> getMonthlyRevenue(int year) {
        List<Object[]> results = orderRepository.getMonthlyRevenue(
                year, PaymentStatus.PAID
        );

        Map<Integer, BigDecimal> map = results.stream()
                .collect(Collectors.toMap(
                        row -> ((Number) row[0]).intValue(),
                        row -> (BigDecimal) row[1]
                ));

        return IntStream.rangeClosed(1, 12)
                .mapToObj(m -> new RevenueDTO(m, map.getOrDefault(m, BigDecimal.ZERO)))
                .toList();
    }


    public List<YearlyRevenueDTO> getYearlyRevenue() {
        return orderRepository.getYearlyRevenue(PaymentStatus.PAID).stream()
                .map(row -> new YearlyRevenueDTO(
                        ((Number) row[0]).intValue(),
                        (BigDecimal) row[1]
                ))
                .collect(Collectors.toList());
    }

    // ===== Top 5 sản phẩm bán chạy =====
    public List<TopProductDTO> getTopSellingProducts() {
        return orderRepository.getTopSellingProducts(PaymentStatus.PAID).stream()
                .limit(5)
                .map(row -> new TopProductDTO(
                        (String) row[0],
                        ((Number) row[1]).longValue()
                ))
                .collect(Collectors.toList());
    }

    // ===== Top khách hàng chi tiêu nhiều nhất =====
    public List<TopUserDTO> getTopSpendingUsers() {
        return orderRepository.getTopSpendingUsers(PaymentStatus.PAID).stream()
                .limit(5)
                .map(row -> new TopUserDTO(
                        (String) row[0],
                        (BigDecimal) row[1]
                ))
                .collect(Collectors.toList());
    }
}
