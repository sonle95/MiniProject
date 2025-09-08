package likeUniquloWeb.controller;

import likeUniquloWeb.dto.request.OrderItemRequest;
import likeUniquloWeb.dto.request.OrderRequest;
import likeUniquloWeb.dto.response.ApiResponse;
import likeUniquloWeb.dto.response.OrderResponse;
import likeUniquloWeb.service.OrderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderController {
    OrderService orderService;

    @PostMapping
    public ApiResponse<OrderResponse> create(@RequestBody OrderRequest request){
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.createOrder(request))
                .build();
    }

    @GetMapping
    public ApiResponse<List<OrderResponse>> getAll(){
        return ApiResponse.<List<OrderResponse>>builder()
                .result(orderService.getAll())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<OrderResponse> getById(@PathVariable Long id){
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.getById(id))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<OrderResponse> update(@PathVariable Long id, @RequestBody OrderRequest request){
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.updateOrder(id,request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id){
        orderService.deleteOrder(id);
        return ApiResponse.<Void>builder()
                .message("deleted")
                .build();
    }

    @PostMapping("/{id}/items")
    public ApiResponse<OrderResponse> addItemToOrder(@PathVariable Long id, @RequestBody OrderItemRequest itemRequest){
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.addItemToOrder(id, itemRequest))
                .build();
    }

    @DeleteMapping("/{orderId}/items/{itemId}")
    public ApiResponse<OrderResponse> removeItemFromOrder(@PathVariable Long orderId, @PathVariable Long itemId){
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.removeItemFromOrder(orderId, itemId))
                .build();
    }

}


