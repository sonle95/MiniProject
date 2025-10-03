package likeUniquloWeb.controller;

import jakarta.validation.Valid;
import likeUniquloWeb.dto.request.CartItemRequest;
import likeUniquloWeb.dto.request.CheckoutRequest;
import likeUniquloWeb.dto.request.OrderRequest;
import likeUniquloWeb.dto.response.CartResponse;
import likeUniquloWeb.dto.response.OrderResponse;
import likeUniquloWeb.service.ShoppingCartService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class ShoppingCartController {
    ShoppingCartService shoppingCart;

    @PostMapping("/add")
    public ResponseEntity<CartResponse> addItemToCart(
            @RequestHeader("Authorization") String token,  @Valid
            @RequestBody CartItemRequest request) {
        CartResponse response = shoppingCart.addItemToCart(token, request);
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/remove/{variantId}")
    public ResponseEntity<CartResponse> removeItem(
            @RequestHeader("Authorization") String token,
            @PathVariable Long variantId) {
        CartResponse response = shoppingCart.removeItem(token, variantId);
        return ResponseEntity.ok(response);
    }


    @PutMapping("/update-variant/{variantId}/{newVariantId}")
    public ResponseEntity<CartResponse> updateVariant(
            @RequestHeader("Authorization") String token,
            @PathVariable Long variantId,
            @PathVariable Long newVariantId) {
        CartResponse response = shoppingCart.updateVariant(token, variantId, newVariantId);
        return ResponseEntity.ok(response);
    }


    @PutMapping("/update-quantity/{variantId}/{quantity}")
    public ResponseEntity<CartResponse> updateQuantity(
            @RequestHeader("Authorization") String token,
            @PathVariable Long variantId,
            @PathVariable int quantity) {
        CartResponse response = shoppingCart.updateItemQuantity(token, variantId, quantity);
        return ResponseEntity.ok(response);
    }


    @GetMapping
    public ResponseEntity<CartResponse> getCart(@RequestHeader("Authorization") String token) {
        CartResponse response = shoppingCart.getCart(token);
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(@RequestHeader("Authorization") String token) {
        shoppingCart.clearCart(token);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> checkoutCart(
            @RequestHeader("Authorization") String token, @Valid
            @RequestBody CheckoutRequest request) {
        OrderResponse response = shoppingCart.checkOutCart(token, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
