package likeUniquloWeb.controller;

import likeUniquloWeb.dto.request.OrderItemRequest;
import likeUniquloWeb.dto.response.ApiResponse;
import likeUniquloWeb.dto.response.OrderItemResponse;
import likeUniquloWeb.service.OrderItemService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/orderItems")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderItemController {
        OrderItemService itemService;

    @PostMapping
    public ApiResponse<OrderItemResponse>
    create(@RequestBody OrderItemRequest request){

        return ApiResponse.<OrderItemResponse>builder()
                .result(itemService.createOrderItem(request))
                .build();
    }

    @GetMapping
    public ApiResponse<List<OrderItemResponse>> getAll() {
        return ApiResponse.<List<OrderItemResponse>>builder()
                .result(itemService.getAllOrderItems())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<OrderItemResponse> getById(@PathVariable Long id){
        return ApiResponse.<OrderItemResponse>builder()
                .result(itemService.getById(id))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<OrderItemResponse> update(@PathVariable Long id, @RequestBody OrderItemRequest itemRequest) throws IOException {
        return ApiResponse.<OrderItemResponse>builder()
                .result(itemService.updateOrderItem(id, itemRequest))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id){
        itemService.deleteById(id);
        return ApiResponse.<Void>builder()
                .message("Deleted!")
                .build();
    }

}
