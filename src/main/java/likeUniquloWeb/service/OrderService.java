package likeUniquloWeb.service;


import likeUniquloWeb.dto.request.OrderItemRequest;
import likeUniquloWeb.dto.request.OrderRequest;
import likeUniquloWeb.dto.response.OrderResponse;
import likeUniquloWeb.entity.Order;
import likeUniquloWeb.entity.OrderItems;
import likeUniquloWeb.entity.ProductVariant;
import likeUniquloWeb.entity.Stock;
import likeUniquloWeb.enums.OrderStatus;
import likeUniquloWeb.exception.AppException;
import likeUniquloWeb.exception.ErrorCode;
import likeUniquloWeb.mapper.OrderMapper;
import likeUniquloWeb.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderService {
    OrderRepository orderRepository;
    OrderMapper orderMapper;
    ProductVariantRepository variantRepository;
    StockRepository stockRepository;
    OrderItemsRepository itemsRepository;

    @Transactional
    public OrderResponse createOrder(OrderRequest request){
        validateOrderRequest(request);
        Order order = orderMapper.orderToEntity(request);

        List<Long> variantIds = request.getOrderItems().stream()
                .map(OrderItemRequest::getProductVariantId).toList();

        Map<Long, ProductVariant> variants = variantRepository.findAllById(variantIds)
                .stream().collect(Collectors.
                        toMap(productVariant -> productVariant.getId(),v->v));

        Map<Long, Stock> stocks = stockRepository.findByProductVariantIdIn(variantIds)
                .stream().collect(Collectors.toMap(stock -> stock.getProductVariant().getId(),stock -> stock));

        for(OrderItemRequest itemRequest: request.getOrderItems()){
            ProductVariant variant = variants.get(itemRequest.getProductVariantId());
            if(variant == null){
                throw new AppException(ErrorCode.NOT_FOUND);
            }

            Stock stock = stocks.get(variant.getId());
            if(stock == null){
                throw new AppException(ErrorCode.NOT_FOUND);
            }

            if(stock.getQuantity() < itemRequest.getQuantity()){
                throw new AppException(ErrorCode.NOT_FOUND);
            }

            stock.setQuantity(stock.getQuantity() - itemRequest.getQuantity());

            OrderItems orderItem = createOrderItem(order, variant, itemRequest.getQuantity());
            order.getOrderItems().add(orderItem);
        }

        stockRepository.saveAll(stocks.values());
        order.setTotalAmount(calculateTotalAmount(order));
        return orderMapper.orderToDto(orderRepository.save(order));

    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getAll(){
        return orderRepository.findAll()
                .stream().map(orderMapper::orderToDto).toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse getById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        return orderMapper.orderToDto(order);
    }

    @Transactional
    public void deleteOrder(Long orderId){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        if(!canDeleteOrder(order)){
            throw new AppException(ErrorCode.NOT_FOUND);
        }

        List<Stock> stocksToUpdate = new ArrayList<>();
        for(OrderItems orderItem: order.getOrderItems()){
            ProductVariant variant = findVariant(orderItem);
            Stock stock = stockRepository.findByProductVariantId(variant.getId())
                    .orElseThrow(()-> new AppException(ErrorCode.NOT_FOUND));

            stock.setQuantity(stock.getQuantity() + orderItem.getQuantity());

            stocksToUpdate.add(stock);
        }
        stockRepository.saveAll(stocksToUpdate);
        orderRepository.delete(order);
    }
    @Transactional
    public OrderResponse updateOrder(Long orderId, OrderRequest request){
             validateOrderRequest(request);
            Order order = orderRepository.findById(orderId).orElseThrow(()-> new AppException(ErrorCode.NOT_FOUND));
            if(!canUpdateOrder(order)){
                throw new AppException(ErrorCode.NOT_FOUND);
            }
            restoreStocksFromOrderItems(order.getOrderItems());
            order.getOrderItems().clear();

            addItemsToOrder(order, request.getOrderItems());
            order.setTotalAmount(calculateTotalAmount(order));
            return orderMapper.orderToDto(orderRepository.save(order));

    }

   @Transactional
    public OrderResponse addItemToOrder(Long orderId, OrderItemRequest itemRequest){
        Order order =  orderRepository.findById(orderId).orElseThrow(()-> new AppException(ErrorCode.NOT_FOUND));

        if(!canUpdateOrder(order)){
            throw  new AppException(ErrorCode.NOT_FOUND);
        }

        ProductVariant variant = variantRepository.findById(itemRequest.getProductVariantId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        Stock stock = stockRepository.findByProductVariantId(variant.getId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));


        if(stock.getQuantity() < itemRequest.getQuantity()){
             throw new AppException(ErrorCode.NOT_FOUND);
        }
        OrderItems item = createOrderItem(order, variant, itemRequest.getQuantity());

        stock.setQuantity(stock.getQuantity() - itemRequest.getQuantity());
        stockRepository.save(stock);

        order.getOrderItems().add(item);

        order.setTotalAmount(calculateTotalAmount(order));
        orderRepository.save(order);

        return orderMapper.orderToDto(order);
    }

    @Transactional
    public OrderResponse removeItemFromOrder(Long orderId, Long itemId){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(()->new AppException(ErrorCode.NOT_FOUND));

        OrderItems orderItem = itemsRepository.findById(itemId)
                .orElseThrow(()->new AppException(ErrorCode.NOT_FOUND));

        if(!order.getOrderItems().contains(orderItem)){
            throw new AppException(ErrorCode.NOT_FOUND);
        }

        if(!canUpdateOrder(order)){
            throw new AppException(ErrorCode.NOT_FOUND);
        }
        ProductVariant variant = findVariant(orderItem);

        Stock stock = stockRepository.findByProductVariantId(variant.getId())
                .orElseThrow(()->new AppException(ErrorCode.NOT_FOUND));

        stock.setQuantity(stock.getQuantity() + orderItem.getQuantity());
        stockRepository.save(stock);

        order.getOrderItems().remove(orderItem);
        itemsRepository.delete(orderItem);

        order.setTotalAmount(calculateTotalAmount(order));
        orderRepository.save(order);

        return orderMapper.orderToDto(order);
    }

    private void validateOrderRequest(OrderRequest orderRequest){
        if(orderRequest.getOrderItems() == null || orderRequest.getOrderItems().isEmpty()){
        throw  new AppException(ErrorCode.NOT_FOUND);
        }
        for(OrderItemRequest itemRequest: orderRequest.getOrderItems()){
            validateOrderItemRequest(itemRequest);
        }
    }

    public void validateOrderItemRequest(OrderItemRequest itemRequest){
        if(itemRequest.getProductVariantId() == null){
            throw  new AppException(ErrorCode.NOT_FOUND);
        }
        if(itemRequest.getQuantity() <=0 ){
            throw  new AppException(ErrorCode.NOT_FOUND);
        }
    }

    private void restoreStocksFromOrderItems(Set<OrderItems> orderItems){
        for(OrderItems item: orderItems){
            ProductVariant variant = findVariant(item);
            Stock stock = stockRepository.findByProductVariantId(variant.getId())
                    .orElseThrow(()->new AppException(ErrorCode.NOT_FOUND));
            stock.setQuantity(stock.getQuantity() + item.getQuantity());
            stockRepository.save(stock);
        }
    }

    private void addItemsToOrder(Order order, List<OrderItemRequest> itemRequests){
        List<Long> variantIds = itemRequests.stream()
                .map(OrderItemRequest::getProductVariantId).toList();
        Map<Long, ProductVariant> variants = variantRepository.findAllById(variantIds)
                .stream().collect(Collectors.toMap(ProductVariant::getId, variant -> variant));

        Map<Long, Stock> stocks = stockRepository.findByProductVariantIdIn(variantIds)
                .stream().collect(Collectors.toMap(stock -> stock.getProductVariant().getId(), stock -> stock));

        for(OrderItemRequest itemRequest: itemRequests){
            ProductVariant variant = variants.get(itemRequest.getProductVariantId());
            if(variant == null) {
                throw new AppException(ErrorCode.NOT_FOUND);
            }
            Stock stock = stocks.get(variant.getId());

            if(stock == null) {
                throw new AppException(ErrorCode.NOT_FOUND);
            }

            if(stock.getQuantity() < itemRequest.getQuantity()){
                throw new AppException(ErrorCode.NOT_FOUND);
            }

            stock.setQuantity(stock.getQuantity() - itemRequest.getQuantity());

            OrderItems orderItem = createOrderItem(order, variant, itemRequest.getQuantity());
            order.getOrderItems().add(orderItem);

        }
        stockRepository.saveAll(stocks.values());

        order.setTotalAmount(calculateTotalAmount(order));
        orderRepository.save(order);
    }

    private boolean canUpdateOrder(Order order){
        return order.getStatus() == OrderStatus.PENDING;
    }

    private ProductVariant findVariant(OrderItems orderItem){
        return orderItem.getProduct().getProductVariants().stream()
                .filter(variant -> variant.getId().equals(orderItem.getProductVariantId()))
                .findFirst().orElseThrow(()->new AppException(ErrorCode.NOT_FOUND));
    }

    private boolean canDeleteOrder(Order order){
        return order.getStatus() == OrderStatus.PENDING
                || order.getStatus() == OrderStatus.CANCELLED
                || order.getStatus() == OrderStatus.PROCESSING;
    }

    private OrderItems createOrderItem(Order order, ProductVariant variant, int quantity){
        OrderItems orderItem = new OrderItems();
        orderItem.setOrder(order);
        orderItem.setProduct(variant.getProduct());
        orderItem.setProductVariantId(variant.getId());
        orderItem.setQuantity(quantity);
        orderItem.setPrice(variant.getPrice());
        return orderItem;
    }

    private BigDecimal calculateTotalAmount(Order order) {
        return order.getOrderItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
