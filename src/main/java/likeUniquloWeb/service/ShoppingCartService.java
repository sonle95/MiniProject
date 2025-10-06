package likeUniquloWeb.service;


import likeUniquloWeb.dto.request.CartItemRequest;
import likeUniquloWeb.dto.request.CheckoutRequest;
import likeUniquloWeb.dto.request.OrderItemRequest;
import likeUniquloWeb.dto.request.OrderRequest;
import likeUniquloWeb.dto.response.CartResponse;
import likeUniquloWeb.dto.response.OrderResponse;
import likeUniquloWeb.entity.Cart;
import likeUniquloWeb.entity.CartItem;
import likeUniquloWeb.entity.ProductVariant;
import likeUniquloWeb.entity.User;
import likeUniquloWeb.exception.AppException;
import likeUniquloWeb.exception.ErrorCode;
import likeUniquloWeb.mapper.CartMapper;
import likeUniquloWeb.repository.CartRepository;
import likeUniquloWeb.repository.ProductVariantRepository;
import likeUniquloWeb.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ShoppingCartService {

    CartRepository cartRepository;
    CartMapper cartMapper;
    OrderService orderService;
    ProductVariantRepository variantRepository;
    UserRepository userRepository;
    AuthenticationService authenticationService;



    //    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Transactional
    public CartResponse addItemToCart(String token, CartItemRequest request){
        validateStockAvailability(request.getProductVariantId(), request.getQuantity());
        if (request.getQuantity() <= 0) {
            throw new AppException(ErrorCode.QUANTITY_MUST_NOT_LESS_THAN_ZERO);
        }
        log.info("test");
        User user = authenticationService.getUserFromToken(token);
        Cart cart = findOrCreateCart(user.getId());
        CartItem existingItem = findCartItem(cart, request.getProductVariantId());
        log.info("test");
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

    //    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Transactional
    public OrderResponse checkOutCart(String token, CheckoutRequest request){
        log.info("Received OrderRequest - addressId: {}", request.getAddressId());
        User user = authenticationService.getUserFromToken(token);
        Cart cart = findCartByUserId(user.getId());

        if (cart.getCartItems().isEmpty()) {
            throw new AppException(ErrorCode.CART_EMPTY);
        }
        validateAllCartItems(cart);
        OrderRequest orderRequest = convertCartToOrderRequest(cart, request, user);

        OrderResponse orderResponse = orderService.createOrder(orderRequest, token);

        cartRepository.delete(cart);
        return orderResponse;
    }

    //    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Transactional
    public CartResponse removeItem(String token, Long cartItemId){
        User user = authenticationService.getUserFromToken(token);
        Cart cart = findCartByUserId(user.getId());

        CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> item.getId().equals(cartItemId)).findFirst()
                .orElseThrow(()-> new AppException(ErrorCode.ITEM_NOT_FOUND));

        cart.getCartItems().remove(cartItem);
        return cartMapper.toDto(cartRepository.save(cart));
    }

    //    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Transactional
    public void clearCart(String token) {
        User user = authenticationService.getUserFromToken(token);
        Cart cart = findCartByUserId(user.getId());
        cart.getCartItems().clear();
        cartRepository.save(cart);
    }

    //    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Transactional
    public CartResponse updateVariant(String token, Long variantId, Long newVariantId){
        User user = authenticationService.getUserFromToken(token);
        Cart cart = findCartByUserId(user.getId());
        CartItem cartItem = cart.getCartItems().stream()
                .filter(item->item.getId().equals(variantId)).findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.ITEM_NOT_FOUND));

        ProductVariant newVariant = variantRepository.findById(newVariantId)
                .orElseThrow(() -> new AppException(ErrorCode.VARIANT_NOT_FOUND));
        validateStockAvailability(newVariantId, cartItem.getQuantity());
        cartItem.setProductVariant(newVariant);
        cartItem.setPrice(newVariant.getPrice());
        cartItem.setProduct(newVariant.getProduct());
        return cartMapper.toDto(cartRepository.save(cart));
    }

    //    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Transactional
    public CartResponse updateItemQuantity(String token, Long cartIemId, int newQuantity) {
        User user = authenticationService.getUserFromToken(token);
        Cart cart = findCartByUserId(user.getId());

        CartItem item = cart.getCartItems().stream()
                .filter(i -> i.getId().equals(cartIemId))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.ITEM_NOT_FOUND));

        if (newQuantity <= 0) {
            throw new AppException(ErrorCode.QUANTITY_MUST_NOT_LESS_THAN_ZERO);
        }

        validateStockAvailability(item.getProductVariant().getId(), newQuantity);

        item.setQuantity(newQuantity);
        return cartMapper.toDto(cartRepository.save(cart));
    }

    //    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
//    @Transactional(readOnly = true)
    public CartResponse getCart(String token) {
        User user = authenticationService.getUserFromToken(token);
        Cart cart = findCartByUserId(user.getId());
        return cartMapper.toDto(cart);
    }


    public void validateStockAvailability(Long variantId, int newQuantity){
        ProductVariant variant = variantRepository.findById(variantId)
                .orElseThrow(()->new AppException(ErrorCode.VARIANT_NOT_FOUND));

        if (variant.getStock() == null) {
            throw new AppException(ErrorCode.INSUFFICIENT_STOCK);
        }
        int variantQuantity = variant.getStock().getQuantity();

        if(newQuantity > variantQuantity){
            throw new AppException(ErrorCode.INSUFFICIENT_STOCK);
        };
    }
    private void validateAllCartItems(Cart cart) {
        for (CartItem item : cart.getCartItems()) {
            validateStockAvailability(item.getProductVariant().getId(), item.getQuantity());
        }
    }

    public OrderRequest convertCartToOrderRequest(Cart cart, CheckoutRequest request, User user){
        List<OrderItemRequest> itemRequests = cart.getCartItems().stream()
                .map(cartItem -> OrderItemRequest.builder()
                        .quantity(cartItem.getQuantity())
                        .productVariantId(cartItem.getProductVariant().getId())
                        .build()).collect(Collectors.toList());
        return OrderRequest.builder()
                .addressId(request.getAddressId())
                .orderItems(itemRequests)
//                .couponId(request.getCouponId())
                .paymentMethod(request.getPaymentMethod())
//                .userId(user.getId())
                .build();
    }


    public Cart findCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(Cart.builder().userId(userId).build()));
    }


    public Cart findOrCreateCart(Long userId){

        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .userId(userId)
                            .build();
                    return cartRepository.save(newCart);
                });
    }

    public CartItem createCartItem(Cart cart, CartItemRequest request){

        ProductVariant variant = variantRepository.findById(request.getProductVariantId())
                .orElseThrow(() -> new AppException(ErrorCode.VARIANT_NOT_FOUND));
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setProduct(variant.getProduct());
        cartItem.setProductVariant(variant);
        cartItem.setQuantity(request.getQuantity());
        cartItem.setPrice(variant.getPrice());
        cart.getCartItems().add(cartItem);
        return cartItem;
    }

    public CartItem findCartItem(Cart cart, Long variantId){
        Optional<CartItem> existingItem = cart.getCartItems().stream()
                .filter(cartItem -> cartItem.getProductVariant().getId().equals(variantId)).findFirst();
        return existingItem.orElse(null);
    }
}
