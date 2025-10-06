package likeUniquloWeb.service;


import likeUniquloWeb.dto.request.OrderItemRequest;
import likeUniquloWeb.dto.request.OrderRequest;
import likeUniquloWeb.dto.response.OrderResponse;
import likeUniquloWeb.entity.*;
import likeUniquloWeb.enums.OrderStatus;
import likeUniquloWeb.enums.PaymentStatus;
import likeUniquloWeb.exception.AppException;
import likeUniquloWeb.exception.ErrorCode;
import likeUniquloWeb.mapper.OrderMapper;
import likeUniquloWeb.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderService {
    OrderRepository orderRepository;
    OrderMapper orderMapper;
    ProductVariantRepository variantRepository;
    StockRepository stockRepository;
    OrderItemsRepository itemsRepository;
    AddressRepository addressRepository;
    AuthenticationService authenticationService;

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Transactional
    public OrderResponse createOrder(OrderRequest request, String token){
        validateOrderRequest(request);
        User user = authenticationService.getUserFromToken(token);
        Order order = orderMapper.orderToEntity(request);
        order.setUser(user);
         Address address = addressRepository.findById(request.getAddressId())
                    .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
         order.setAddress(address);
        order.getOrderItems().clear();
        List<Long> variantIds = request.getOrderItems().stream()
                .map(OrderItemRequest::getProductVariantId).toList();

        Map<Long, ProductVariant> variants = variantRepository.findAllById(variantIds)
                .stream().collect(Collectors.
                        toMap(productVariant -> productVariant.getId(),v->v));
        log.info("Variant in DB = {}", variants);
        Map<Long, Stock> stocks = stockRepository.findByProductVariant_IdIn(variantIds)
                .stream().collect(Collectors.
                        toMap(stock ->
                                stock.getProductVariant().getId(),stock -> stock));
        log.info("Stock map keys: {}", stocks.keySet());

        for(OrderItemRequest itemRequest: request.getOrderItems()){
            ProductVariant variant = variants.get(itemRequest.getProductVariantId());
            log.info("VariantId from request = {}", itemRequest.getProductVariantId());
            if(variant == null){
                throw new AppException(ErrorCode.VARIANT_MUST_NOT_BE_NULL);
            }

            Stock stock = stocks.get(itemRequest.getProductVariantId());
            if(stock == null){
                throw new AppException(ErrorCode.STOCK_MUST_NOT_BE_NULL);
            }
            log.info("Stocks map keys = {}", stocks.keySet());

            if(stock.getQuantity() < itemRequest.getQuantity()){
                throw new AppException(ErrorCode.INSUFFICIENT_STOCK);
            }

            stock.setQuantity(stock.getQuantity() - itemRequest.getQuantity());

            OrderItems orderItem = createOrderItem(order, variant, itemRequest.getQuantity());
            orderItem.setOrder(order);
            order.getOrderItems().add(orderItem);

            log.info("Added OrderItem: variant={}, qty={}, price={}",
                    orderItem.getProductVariant().getId(),
                    orderItem.getQuantity(),
                    orderItem.getPrice());

            order.getOrderItems().forEach(oi -> {
                log.info("DEBUG OrderItem: variant={}, qty={}, price={}, order={}",
                        oi.getProductVariant().getId(),
                        oi.getQuantity(),
                        oi.getPrice(),
                        oi.getOrder() != null ? oi.getOrder().getId() : "NULL");
            });
        }
        stockRepository.saveAll(stocks.values());
        order.setTotalAmount(calculateTotalAmount(order));
        return orderMapper.orderToDto(orderRepository.save(order));

    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Transactional(readOnly = true)
    public List<OrderResponse> getAll(){
        return orderRepository.findAll()
                .stream().map(orderMapper::orderToDto).toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Transactional(readOnly = true)
    public OrderResponse getById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        return orderMapper.orderToDto(order);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Transactional
    public void deleteOrder(Long orderId){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        if(!canDeleteOrder(order)){
            throw new AppException(ErrorCode.CAN_NOT_DELETE_ORDER);
        }
        List<Stock> stocksToUpdate = new ArrayList<>();
        for(OrderItems orderItem: order.getOrderItems()){
            ProductVariant variant = findVariant(orderItem);
            Stock stock = stockRepository.findByProductVariant_Id(variant.getId())
                    .orElseThrow(()-> new AppException(ErrorCode.STOCK_NOT_FOUND));

            stock.setQuantity(stock.getQuantity() + orderItem.getQuantity());

            stocksToUpdate.add(stock);
        }
        stockRepository.saveAll(stocksToUpdate);
        orderRepository.delete(order);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Transactional
    public OrderResponse updateOrder(Long orderId, OrderRequest request){
        log.info("first test");
             validateOrderRequest(request);
        log.info("second test");
             Order order = orderRepository.findById(orderId).orElseThrow(()-> new AppException(ErrorCode.ORDER_NOT_FOUND));
             log.info("second test");
            if(!canUpdateOrder(order)){
                throw new AppException(ErrorCode.CAN_NOT_UPDATE_ORDER);
            }
            restoreStocksFromOrderItems(order.getOrderItems());
            order.getOrderItems().clear();

            addItemsToOrder(order, request.getOrderItems());
            order.setTotalAmount(calculateTotalAmount(order));
            return orderMapper.orderToDto(orderRepository.save(order));

    }


    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
   @Transactional
    public OrderResponse addItemToOrder(Long orderId, OrderItemRequest itemRequest){
        Order order =  orderRepository.findById(orderId).orElseThrow(()-> new AppException(ErrorCode.NOT_FOUND));

        if(!canUpdateOrder(order)){
            throw  new AppException(ErrorCode.CAN_NOT_UPDATE_ORDER);
        }

        ProductVariant variant = variantRepository.findById(itemRequest.getProductVariantId())
                .orElseThrow(() -> new AppException(ErrorCode.VARIANT_NOT_FOUND));

        Stock stock = stockRepository.findByProductVariant_Id(variant.getId())
                .orElseThrow(() -> new AppException(ErrorCode.STOCK_NOT_FOUND));


        if(stock.getQuantity() < itemRequest.getQuantity()){
             throw new AppException(ErrorCode.INSUFFICIENT_STOCK);
        }
        OrderItems item = createOrderItem(order, variant, itemRequest.getQuantity());

        stock.setQuantity(stock.getQuantity() - itemRequest.getQuantity());
        stockRepository.save(stock);

        order.getOrderItems().add(item);

        order.setTotalAmount(calculateTotalAmount(order));
        orderRepository.save(order);

        return orderMapper.orderToDto(order);
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Transactional
    public OrderResponse removeItemFromOrder(Long orderId, Long itemId){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(()->new AppException(ErrorCode.ORDER_NOT_FOUND));

        OrderItems orderItem = itemsRepository.findById(itemId)
                .orElseThrow(()->new AppException(ErrorCode.ITEM_NOT_FOUND));

        if(!order.getOrderItems().contains(orderItem)){
            throw new AppException(ErrorCode.ITEM_NOT_FOUND);
        }

        if(!canUpdateOrder(order)){
            throw new AppException(ErrorCode.CAN_NOT_UPDATE_ORDER);
        }
        ProductVariant variant = findVariant(orderItem);

        Stock stock = stockRepository.findByProductVariant_Id(variant.getId())
                .orElseThrow(()->new AppException(ErrorCode.STOCK_NOT_FOUND));

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
        throw  new AppException(ErrorCode.ORDER_MUST_NOT_BE_NULL);
        }
        for(OrderItemRequest itemRequest: orderRequest.getOrderItems()){
            validateOrderItemRequest(itemRequest);
        }
    }

    public void validateOrderItemRequest(OrderItemRequest itemRequest){
        if(itemRequest.getProductVariantId() == null){
            throw  new AppException(ErrorCode.ITEM_MUST_NOT_BE_NULL);
        }
        if(itemRequest.getQuantity() <=0 ){
            throw  new AppException(ErrorCode.QUANTITY_MUST_NOT_LESS_THAN_ZERO);
        }
    }

    private void restoreStocksFromOrderItems(Set<OrderItems> orderItems){
        log.info("test");
        for(OrderItems item: orderItems){
            ProductVariant variant = findVariant(item);
            Stock stock = stockRepository.findByProductVariant_Id(variant.getId())
                    .orElseThrow(()->new AppException(ErrorCode.STOCK_NOT_FOUND));
            log.info("stock = {}" + stock);
            stock.setQuantity(stock.getQuantity() + item.getQuantity());
            stockRepository.save(stock);
        }
    }

    private void addItemsToOrder(Order order, List<OrderItemRequest> itemRequests){
        List<Long> variantIds = itemRequests.stream()
                .map(OrderItemRequest::getProductVariantId).toList();
        Map<Long, ProductVariant> variants = variantRepository.findAllById(variantIds)
                .stream().collect(Collectors.toMap(ProductVariant::getId, variant -> variant));

        Map<Long, Stock> stocks = stockRepository.findByProductVariant_IdIn(variantIds)
                .stream().collect(Collectors.toMap(stock -> stock.getProductVariant().getId(), stock -> stock));

        for(OrderItemRequest itemRequest: itemRequests){
            ProductVariant variant = variants.get(itemRequest.getProductVariantId());
            if(variant == null) {
                throw new AppException(ErrorCode.VARIANT_MUST_NOT_BE_NULL);
            }
            Stock stock = stocks.get(variant.getId());

            if(stock == null) {
                throw new AppException(ErrorCode.STOCK_MUST_NOT_BE_NULL);
            }

            if(stock.getQuantity() < itemRequest.getQuantity()){
                throw new AppException(ErrorCode.INSUFFICIENT_STOCK);
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
        return orderItem.getProductVariant();
    }

    private boolean canDeleteOrder(Order order){
        return order.getStatus() == OrderStatus.PENDING
                || order.getStatus() == OrderStatus.CANCELLED
                || order.getStatus() == OrderStatus.PROCESSING;
    }

    private OrderItems createOrderItem(Order order, ProductVariant variant, int quantity){
        OrderItems orderItem = new OrderItems();
        orderItem.setOrder(order);
        orderItem.setProductVariant(variant);
        log.info("Created OrderItem: variant={}, qty={}, price={}, order={}",
                orderItem.getProductVariant() != null ? orderItem.getProductVariant().getId() : "NULL",
                orderItem.getQuantity(),
                orderItem.getPrice(),
                orderItem.getOrder() != null ? orderItem.getOrder().getId() : "NULL"
        );

        orderItem.setQuantity(quantity);
        orderItem.setPrice(variant.getPrice());
        log.info("Variant {} has price = {}", variant.getId(), variant.getPrice());
        return orderItem;
    }

    private BigDecimal calculateTotalAmount(Order order) {
        return order.getOrderItems().stream()
                .filter(item -> item.getProductVariant() != null && item.getProductVariant().getPrice() != null)
                .map(item -> item.getProductVariant().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Không thể cập nhật đơn hàng đã kết thúc");
        }

        order.setStatus(newStatus);

        if (newStatus == OrderStatus.CANCELLED && order.getPaymentStatus() == PaymentStatus.PAID) {
            order.setPaymentStatus(PaymentStatus.REFUNDED);
        }

        return orderMapper.orderToDto(orderRepository.save(order));
    }


    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public OrderResponse updatePaymentStatus(Long orderId, PaymentStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        order.setPaymentStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());

        return orderMapper.orderToDto(orderRepository.save(order));
    }

    public Page<OrderResponse> getOrdersByPage(int page, int size, String sortDir){
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by("createdAt").ascending().and(Sort.by("id").ascending())
                : Sort.by("createdAt").descending().and(Sort.by("id").descending());

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Order> orders = orderRepository.findAll(pageable);
        return orders.map(orderMapper::orderToDto);
    }


}
