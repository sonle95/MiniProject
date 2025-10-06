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
    public OrderItemResponse
    create(@RequestBody OrderItemRequest request){

        return itemService.createOrderItem(request);
    }

    @GetMapping
    public List<OrderItemResponse> getAll() {
        return itemService.getAllOrderItems();
    }

    @GetMapping("/{id}")
    public OrderItemResponse getById(@PathVariable Long id){
        return itemService.getById(id);
    }

    @PutMapping("/{id}")
    public OrderItemResponse update(@PathVariable Long id,
                                                 @RequestBody OrderItemRequest itemRequest)
            throws IOException {
        return itemService.updateOrderItem(id, itemRequest);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        itemService.deleteById(id);
    }

}
