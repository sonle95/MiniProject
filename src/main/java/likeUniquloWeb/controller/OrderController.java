package likeUniquloWeb.controller;

import likeUniquloWeb.dto.request.OrderItemRequest;
import likeUniquloWeb.dto.request.OrderRequest;
import likeUniquloWeb.dto.request.PaymentUpdateRequest;
import likeUniquloWeb.dto.request.StatusUpdateRequest;
import likeUniquloWeb.dto.response.ApiResponse;
import likeUniquloWeb.dto.response.OrderResponse;
import likeUniquloWeb.enums.OrderStatus;
import likeUniquloWeb.exception.AppException;
import likeUniquloWeb.exception.ErrorCode;
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
@CrossOrigin(origins = "*")
public class OrderController {
    OrderService orderService;

    @PostMapping
    public OrderResponse create(@RequestBody OrderRequest request,  @RequestHeader("Authorization") String token){
        String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        return orderService.createOrder(request, jwtToken);
    }

    @GetMapping
    public List<OrderResponse> getAll(){
        return orderService.getAll();
    }

    @GetMapping("/{id}")
    public OrderResponse getById(@PathVariable Long id){
        return orderService.getById(id);
    }

    @PutMapping("/{id}")
    public OrderResponse update(@PathVariable Long id, @RequestBody OrderRequest request){
        return orderService.updateOrder(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        orderService.deleteOrder(id);
    }

    @PostMapping("/{id}/items")
    public OrderResponse addItemToOrder(@PathVariable Long id, @RequestBody OrderItemRequest itemRequest){
        return orderService.addItemToOrder(id, itemRequest);
    }

    @DeleteMapping("/{orderId}/items/{itemId}")
    public OrderResponse removeItemFromOrder(@PathVariable Long orderId, @PathVariable Long itemId){
        return orderService.removeItemFromOrder(orderId, itemId);
    }

    @PutMapping("/{orderId}/status")
    public OrderResponse updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody StatusUpdateRequest request
    ) {
        return orderService.updateOrderStatus(orderId, request.getNewStatus());
    }

    @PutMapping("/{orderId}/payment-status")
    public OrderResponse updatePaymentStatus(
            @PathVariable Long orderId,
            @RequestBody PaymentUpdateRequest request
    ) {
        return orderService.updatePaymentStatus(orderId, request.getPaymentStatus());
    }

}


