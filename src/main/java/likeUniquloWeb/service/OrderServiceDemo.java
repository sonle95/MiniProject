package likeUniquloWeb.service;

import likeUniquloWeb.dto.request.OrderItemRequest;
import likeUniquloWeb.dto.request.OrderRequest;
import likeUniquloWeb.dto.response.OrderResponse;
import likeUniquloWeb.entity.Order;
import likeUniquloWeb.entity.OrderItems;
import likeUniquloWeb.entity.ProductVariant;
import likeUniquloWeb.entity.Stock;
import likeUniquloWeb.exception.AppException;
import likeUniquloWeb.exception.ErrorCode;
import likeUniquloWeb.mapper.OrderMapper;
import likeUniquloWeb.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderServiceDemo {

    OrderRepository orderRepository;
    OrderMapper orderMapper;
    ProductVariantRepository variantRepository;
    StockRepository stockRepository;
    OrderItemsRepository itemsRepository;

    public OrderResponse createOrder(OrderRequest request){
        Order order = orderMapper.orderToEntity(request);

        for(OrderItemRequest item: request.getOrderItems()){
            ProductVariant variant = variantRepository
                    .findById(item.getProductVariantId()).orElseThrow(() ->
                            new AppException(ErrorCode.NOT_FOUND));

            Stock stock = stockRepository.findByProductVariantId(variant.getId()).orElseThrow(
                    ()-> new AppException(ErrorCode.NOT_FOUND));

            if(stock.getQuantity() < item.getQuantity()){
                throw  new AppException(ErrorCode.NOT_FOUND);
            }

            stock.setQuantity(stock.getQuantity() - item.getQuantity());
            stockRepository.save(stock);

            OrderItems orderItem = new OrderItems();

            orderItem.setOrder(order);
            orderItem.setProduct(variant.getProduct());
            orderItem.setProductVariantId(variant.getId());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPrice(variant.getProduct().getPrice());

            order.getOrderItems().add(orderItem);
        }
        order.setTotalAmount(calculateTotalAmount(order));

        return orderMapper.orderToDto(orderRepository.save(order));
    }

    public List<OrderResponse> getAll(){
        return orderRepository.findAll()
                .stream().map(orderMapper::orderToDto).toList();
    }

    public OrderResponse getById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        return orderMapper.orderToDto(order);
    }

    public  void delete(Long id){
        Order order = orderRepository.findById(id) .orElseThrow(()
                -> new AppException(ErrorCode.NOT_FOUND));

        for(OrderItems item: order.getOrderItems()){
            Long variantId = item.getProduct().getProductVariants().stream()
                    .map(productVariant -> productVariant.getId()).findFirst()
                    .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

            Stock stock = stockRepository.findByProductVariantId(variantId)
                    .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

            stock.setQuantity(stock.getQuantity() + item.getQuantity());
            stockRepository.save(stock);
        }

        orderRepository.delete(order);
    }

    public OrderResponse updateOrder(Long id, OrderRequest request){
        Order order = orderRepository.findById(id).orElseThrow(()
                -> new AppException(ErrorCode.NOT_FOUND));

        for(OrderItems item: order.getOrderItems()){
            Long variantId = item.getProduct().getProductVariants().stream()
                    .map(productVariant -> productVariant.getId()).findFirst()
                    .orElseThrow((() -> new AppException(ErrorCode.NOT_FOUND)));
            Stock stock = stockRepository.findByProductVariantId(variantId)
                    .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

            stock.setQuantity(stock.getQuantity() + item.getQuantity());
            stockRepository.save(stock);
        }
        order.getOrderItems().clear();

        for(OrderItemRequest itemRequest: request.getOrderItems()){
            ProductVariant variant = variantRepository
                    .findById(itemRequest.getProductVariantId()).orElseThrow(()
                            -> new AppException(ErrorCode.NOT_FOUND));

            Stock stock = stockRepository.findByProductVariantId(variant.getId())
                    .orElseThrow(()-> new AppException(ErrorCode.NOT_FOUND));

            if(stock.getQuantity() < itemRequest.getQuantity()){
                throw new AppException(ErrorCode.NOT_FOUND);
            }
            stock.setQuantity(stock.getQuantity() - itemRequest.getQuantity());
            stockRepository.save(stock);

            OrderItems orderItem = new OrderItems();
            orderItem.setOrder(order);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setProduct(variant.getProduct());
            orderItem.setProductVariantId(variant.getId());
            orderItem.setPrice(variant.getProduct().getPrice());

            order.getOrderItems().add(orderItem);
        }
        order.setTotalAmount(calculateTotalAmount(order));
        return orderMapper.orderToDto(orderRepository.save(order));
    }

    public OrderResponse addItemToOrder(Long orderId, OrderItemRequest itemRequest){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(()->new AppException(ErrorCode.NOT_FOUND));

        ProductVariant variant = variantRepository.findById(itemRequest.getProductVariantId()
                ).orElseThrow(()-> new AppException(ErrorCode.NOT_FOUND));

        Stock stock = stockRepository.findByProductVariantId(variant.getId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        if(itemRequest.getQuantity() > stock.getQuantity()){
            throw new AppException(ErrorCode.NOT_FOUND);
        }
        stock.setQuantity(stock.getQuantity() - itemRequest.getQuantity());
        stockRepository.save(stock);

        OrderItems orderItem = new OrderItems();
        orderItem.setOrder(order);
        orderItem.setProduct(variant.getProduct());
        orderItem.setProductVariantId(variant.getId());
        orderItem.setQuantity(itemRequest.getQuantity());
        orderItem.setPrice(variant.getProduct().getPrice());

        order.getOrderItems().add(orderItem);
        order.setTotalAmount(calculateTotalAmount(order));
        orderRepository.save(order);

        return orderMapper.orderToDto(order);
    }

    public OrderResponse removeItemFromOrder(Long orderId, Long orderItemId){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(()-> new AppException(ErrorCode.NOT_FOUND));

        OrderItems orderItem  = itemsRepository.findById(orderItemId)
                .orElseThrow(()-> new AppException(ErrorCode.NOT_FOUND));

        if(!order.getOrderItems().contains(orderItem)){
            throw new AppException(ErrorCode.NOT_FOUND);
        }
        Long variantId = orderItem.getProduct().getProductVariants()
                .stream().map(productVariant -> productVariant.getId())
                .findFirst().orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        Stock stock = stockRepository.findByProductVariantId(variantId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        stock.setQuantity(stock.getQuantity() + orderItem.getQuantity());
        stockRepository.save(stock);

        order.getOrderItems().remove(orderItem);

        itemsRepository.delete(orderItem);

        order.setTotalAmount(calculateTotalAmount(order));
        orderRepository.save(order);

        return orderMapper.orderToDto(order);
    }



    private BigDecimal calculateTotalAmount(Order order) {
        return order.getOrderItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
