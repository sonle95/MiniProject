package likeUniquloWeb.controller;

import likeUniquloWeb.dto.request.ReviewRequest;
import likeUniquloWeb.dto.request.StockRequest;
import likeUniquloWeb.dto.response.ApiResponse;
import likeUniquloWeb.dto.response.ReviewResponse;
import likeUniquloWeb.dto.response.StockResponse;
import likeUniquloWeb.service.StockService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stocks")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StockController {

    StockService stockService;


    @PostMapping
    public ApiResponse<StockResponse> create(@RequestBody StockRequest request){
        return ApiResponse.<StockResponse>builder()
                .result(stockService.createStock(request))
                .build();
    }

    @GetMapping
    public ApiResponse<List<StockResponse>> getAll(){
        return ApiResponse.<List<StockResponse>>builder()
                .result(stockService.getAll())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<StockResponse> getById(@PathVariable Long id){
        return ApiResponse.<StockResponse>builder()
                .result(stockService.getById(id))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<StockResponse> update(@RequestBody StockRequest request,
                                              @PathVariable Long id){
        return ApiResponse.<StockResponse>builder()
                .result(stockService.update(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id){
        stockService.delete(id);
        return ApiResponse.<Void>builder()
                .message("deleted")
                .build();
    }
}
