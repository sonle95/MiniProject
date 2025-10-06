package likeUniquloWeb.controller;

import likeUniquloWeb.dto.response.RevenueDTO;
import likeUniquloWeb.dto.response.TopProductDTO;
import likeUniquloWeb.dto.response.TopUserDTO;
import likeUniquloWeb.dto.response.YearlyRevenueDTO;
import likeUniquloWeb.service.StatisticService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatisticController {

    StatisticService statisticService;

    @GetMapping("/revenue")
    public List<RevenueDTO> getMonthlyRevenue(
            @RequestParam(defaultValue = "2025") int year
    ) {
        return statisticService.getMonthlyRevenue(year);
    }

    // Doanh thu theo từng năm
    @GetMapping("/revenue/yearly")
    public List<YearlyRevenueDTO> getYearlyRevenue() {
        return statisticService.getYearlyRevenue();
    }

    // Top 5 sản phẩm bán chạy
    @GetMapping("/revenue/top-products")
    public List<TopProductDTO> getTopSellingProducts() {
        return statisticService.getTopSellingProducts();
    }

    // Top khách hàng chi tiêu nhiều nhất
    @GetMapping("/revenue/top-users")
    public List<TopUserDTO> getTopSpendingUsers() {
        return statisticService.getTopSpendingUsers();
    }

}
