package likeUniquloWeb.service;

import likeUniquloWeb.dto.request.CartItemRequest;
import likeUniquloWeb.dto.request.OrderItemRequest;
import likeUniquloWeb.dto.request.OrderRequest;
import likeUniquloWeb.dto.response.CartResponse;
import likeUniquloWeb.dto.response.OrderResponse;
import likeUniquloWeb.entity.Cart;
import likeUniquloWeb.entity.CartItem;
import likeUniquloWeb.entity.ProductVariant;
import likeUniquloWeb.exception.AppException;
import likeUniquloWeb.exception.ErrorCode;
import likeUniquloWeb.mapper.CartMapper;
import likeUniquloWeb.repository.CartRepository;
import likeUniquloWeb.repository.ProductVariantRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartService {

    CartRepository cartRepository;
    CartMapper cartMapper;
    OrderService orderService;
    ProductVariantRepository variantRepository;

    @Transactional
    public CartResponse addItemToCart(String sessionId, CartItemRequest request){
        validateStockAvailability(request.getProductVariantId(), request.getQuantity());
        if (request.getQuantity() <= 0) {
            throw new AppException(ErrorCode.NOT_FOUND);
        }
        Cart cart = findOrCreateCart(sessionId);

        CartItem existingItem = findCartItem(cart, request.getProductVariantId());

        if(existingItem != null){
            int newQuantity = existingItem.getQuantity() + request.getQuantity();
            validateStockAvailability(request.getProductVariantId(), newQuantity);
            existingItem.setQuantity(newQuantity);
        }else {
            validateStockAvailability(request.getProductVariantId(), request.getQuantity());
            CartItem cartItem = createCartItem(cart, request);
            cart.getCartItems().add(cartItem);
        }

        return cartMapper.toDto(cartRepository.save(cart));

    }

    @Transactional
    public OrderResponse checkOutCart(String sessionId, OrderRequest request){
        Cart cart = findCartBySessionId(sessionId);

        if (cart.getCartItems().isEmpty()) {
            throw new AppException(ErrorCode.NOT_FOUND);
        }
        validateAllCartItems(cart);
        OrderRequest orderRequest = convertCartToOrderRequest(cart, request);

        OrderResponse orderResponse = orderService.createOrder(orderRequest);

        cartRepository.delete(cart);
        return orderResponse;
    }

    @Transactional
    public CartResponse updateCartItem(String sessionId, Long variantId, int newQuantity){
        Cart cart = findCartBySessionId(sessionId);
        CartItem cartItem = findCartItem(cart, variantId);
        if (cartItem == null) {
            throw new AppException(ErrorCode.NOT_FOUND);
        }

        if(newQuantity <= 0){
            cart.getCartItems().remove(cartItem);
            return cartMapper.toDto(cartRepository.save(cart));
        }
        validateStockAvailability(variantId, newQuantity);
        cartItem.setQuantity(newQuantity);
        return cartMapper.toDto(cartRepository.save(cart));
    }

    @Transactional
    public CartResponse removeItem(String sessionId, Long variantId){
        Cart cart = findCartBySessionId(sessionId);

        CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> item.getProductVariantId().equals(variantId)).findFirst()
                .orElseThrow(()-> new AppException(ErrorCode.NOT_FOUND));

        cart.getCartItems().remove(cartItem);
        return cartMapper.toDto(cartRepository.save(cart));
    }

    @Transactional
    public void clearCart(String sessionId) {
        Cart cart = findCartBySessionId(sessionId);
        cart.getCartItems().clear();
        cartRepository.save(cart);
    }

    @Transactional
    public CartResponse updateVariant(String sessionId, Long variantId, Long newVariantId){
        Cart cart = findCartBySessionId(sessionId);
        CartItem cartItem = cart.getCartItems().stream()
                .filter(item->item.getProductVariantId().equals(variantId)).findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        ProductVariant newVariant = variantRepository.findById(newVariantId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        validateStockAvailability(newVariantId, cartItem.getQuantity());
        cartItem.setProductVariantId(newVariantId);
        cartItem.setPrice(newVariant.getPrice());
        cartItem.setProduct(newVariant.getProduct());
        return cartMapper.toDto(cartRepository.save(cart));
    }

    @Transactional
    public CartResponse updateItemQuantity(String sessionId, Long itemId, int newQuantity) {
        Cart cart = findCartBySessionId(sessionId);

        CartItem item = cart.getCartItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        if (newQuantity <= 0) {
            throw new AppException(ErrorCode.NOT_FOUND);
        }

        validateStockAvailability(item.getProductVariantId(), newQuantity);

        item.setQuantity(newQuantity);
        return cartMapper.toDto(cartRepository.save(cart));
    }

    @Transactional(readOnly = true)
    public CartResponse getCart(String sessionId) {
        Cart cart = findCartBySessionId(sessionId);
        return cartMapper.toDto(cart);
    }


    public void validateStockAvailability(Long variantId, int newQuantity){
        ProductVariant variant = variantRepository.findById(variantId)
                .orElseThrow(()->new AppException(ErrorCode.NOT_FOUND));

        if (variant.getStocks() == null || variant.getStocks().isEmpty()) {
            throw new AppException(ErrorCode.NOT_FOUND);
        }
        int variantQuantity = variant.getStocks().stream().
        mapToInt(stock -> stock.getQuantity()).sum();

        if(newQuantity > variantQuantity){
            throw new AppException(ErrorCode.NOT_FOUND);
        };
    }
    private void validateAllCartItems(Cart cart) {
        for (CartItem item : cart.getCartItems()) {
            validateStockAvailability(item.getProductVariantId(), item.getQuantity());
        }
    }

    public OrderRequest convertCartToOrderRequest(Cart cart, OrderRequest request){
        List<OrderItemRequest> itemRequests = cart.getCartItems().stream()
                .map(cartItem -> OrderItemRequest.builder()
                        .quantity(cartItem.getQuantity())
                        .productVariantId(cartItem.getProductVariantId())
                        .build()).collect(Collectors.toList());

        return OrderRequest.builder()
                .orderItems(itemRequests)
                .couponId(request.getCouponId())
                .paymentMethod(request.getPaymentMethod())
                .userId(request.getUserId())
                .build();
    }

    public Cart findCartBySessionId(String sessionId) {
        return cartRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
    }

    public Cart findOrCreateCart(String sessionId){
        return cartRepository.findBySessionId(sessionId)
                .orElseGet(()->{
                    Cart newCart = Cart.builder()
                            .sessionId(sessionId != null ? sessionId : UUID.randomUUID().toString())
                            .expiresAt(LocalDateTime.now().plusHours(24))
                            .build();
                    return cartRepository.save(newCart);
                });
    }

    public CartItem createCartItem(Cart cart, CartItemRequest request){

        ProductVariant variant = variantRepository.findById(request.getProductVariantId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setProductVariantId(request.getProductVariantId());
        cartItem.setQuantity(request.getQuantity());
        cartItem.setPrice(variant.getPrice());
        cartItem.setProduct(variant.getProduct());
        return cartItem;
    }

    public CartItem findCartItem(Cart cart, Long variantId){
        Optional<CartItem> existingItem = cart.getCartItems().stream()
                .filter(cartItem -> cartItem.getProductVariantId().equals(variantId)).findFirst();
        return existingItem.orElse(null);
    }
}
