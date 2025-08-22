package likeUniquloWeb.service;

import likeUniquloWeb.dto.request.OrderRequest;
import likeUniquloWeb.dto.response.OrderResponse;
import likeUniquloWeb.entity.Order;
import likeUniquloWeb.mapper.OrderItemMapper;
import likeUniquloWeb.mapper.OrderMapper;
import likeUniquloWeb.repository.OrderRepository;
import likeUniquloWeb.repository.ProductRepository;
import likeUniquloWeb.repository.ProductVariantRepository;
import likeUniquloWeb.repository.StockRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderService {

    OrderRepository orderRepository;
    OrderMapper orderMapper;
    OrderItemMapper itemMapper;
    ProductRepository productRepository;
    ProductVariantRepository variantRepository;
    StockRepository stockRepository;

    public OrderResponse createOrder(OrderRequest request){
        Order order = orderMapper.orderToEntity(request);

    }



}
