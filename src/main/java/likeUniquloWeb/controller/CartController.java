package likeUniquloWeb.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import likeUniquloWeb.dto.request.CartItemRequest;
import likeUniquloWeb.dto.request.OrderRequest;
import likeUniquloWeb.dto.request.UpdateQuantityRequest;
import likeUniquloWeb.dto.request.UpdateVariantRequest;
import likeUniquloWeb.dto.response.ApiResponse;
import likeUniquloWeb.dto.response.CartResponse;
import likeUniquloWeb.dto.response.OrderResponse;
import likeUniquloWeb.service.CartService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartController {

    CartService cartService;

    @PostMapping
    public ApiResponse<CartResponse> addItemToCart(HttpSession session
    , @RequestBody CartItemRequest request){
        return ApiResponse.<CartResponse>builder()
                .result(cartService.addItemToCart(session.getId(),request))
                .build();
    }

    @GetMapping
    public ApiResponse<CartResponse> getCart(HttpSession session){
        return ApiResponse.<CartResponse>builder()
                .result(cartService.getCart(session.getId()))
                .build();
    }

    @PostMapping("/checkout")
    public ApiResponse<OrderResponse> checkout(HttpSession session,
                                               @RequestBody OrderRequest request){
        return ApiResponse.<OrderResponse>builder()
                .result(cartService.checkOutCart(session.getId(),request))
                .build();
    }

    @PutMapping("/items/{variantId}")
    public ApiResponse<CartResponse>
    updateCartItem(HttpSession session,@PathVariable Long variantId, @RequestBody int newQuantity){
        return ApiResponse.<CartResponse>builder()
                .result(cartService.updateCartItem(session.getId(), variantId, newQuantity))
                .build();
    }

    @PutMapping("/items/{itemId}/quantity")
    public ApiResponse<CartResponse> updateQuantity(HttpSession session,
                                       @PathVariable Long itemId,
                                       @RequestBody @Valid UpdateQuantityRequest quantityRequest){
        return ApiResponse.<CartResponse>builder()
                .result(cartService.updateItemQuantity(session.getId(),itemId, quantityRequest.getQuantity()))
                .build();
    }

    @PutMapping("/items/{itemId}/variant")
    public ApiResponse<CartResponse> updateVariant(
            HttpSession session,
            @PathVariable Long itemId,
            @RequestBody @Valid UpdateVariantRequest request) {
        return ApiResponse.<CartResponse>builder()
                .result(cartService.updateVariant(session.getId(), itemId, request.getVariantId()))
                .build();
    }

    @DeleteMapping("/clear")
    public ApiResponse<Void> clearCart(HttpSession session) {
        cartService.clearCart(session.getId());
        return ApiResponse.<Void>builder()
                .build();
    }

    @DeleteMapping("/items/{itemId}")
    public ApiResponse<CartResponse> removeItem(HttpSession session, @PathVariable Long itemId) {
        return ApiResponse.<CartResponse>builder()
                .result(cartService.removeItem(session.getId(), itemId))
                .build();
    }


}
