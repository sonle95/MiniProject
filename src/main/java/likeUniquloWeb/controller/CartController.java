//package likeUniquloWeb.controller;
//
//import jakarta.servlet.http.HttpSession;
//import jakarta.validation.Valid;
//import likeUniquloWeb.dto.request.CartItemRequest;
//import likeUniquloWeb.dto.request.OrderRequest;
//import likeUniquloWeb.dto.request.UpdateQuantityRequest;
//import likeUniquloWeb.dto.request.UpdateVariantRequest;
//import likeUniquloWeb.dto.response.ApiResponse;
//import likeUniquloWeb.dto.response.CartResponse;
//import likeUniquloWeb.dto.response.OrderResponse;
//import likeUniquloWeb.service.CartService;
//import lombok.AccessLevel;
//import lombok.RequiredArgsConstructor;
//import lombok.experimental.FieldDefaults;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/carts")
//@RequiredArgsConstructor
//@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
//@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
//public class CartController {
//
//    CartService cartService;
//
//    @PostMapping
//    public CartResponse addItemToCart(HttpSession session
//    , @RequestBody CartItemRequest request){
//        return cartService.addItemToCart(session.getId(), request);
//    }
//
//    @GetMapping
//    public CartResponse getCart(HttpSession session){
//        return cartService.getCart(session.getId());
//    }
//
//    @PostMapping("/checkout")
//    public OrderResponse checkout(HttpSession session,
//                                               @RequestBody OrderRequest request){
//        return cartService.checkOutCart(session.getId(),request);
//    }
//
//    @PutMapping("/items/{variantId}")
//    public CartResponse
//    updateCartItem(HttpSession session,@PathVariable Long variantId, @RequestBody int newQuantity){
//        return cartService.updateCartItem(session.getId(), variantId, newQuantity);
//    }
//
//    @PutMapping("/items/{itemId}/quantity")
//    public CartResponse updateQuantity(HttpSession session,
//                                       @PathVariable Long itemId,
//                                       @RequestBody @Valid UpdateQuantityRequest quantityRequest){
//        return cartService.updateItemQuantity(session.getId(),itemId, quantityRequest.getQuantity());
//    }
//
//    @PutMapping("/items/{itemId}/variant")
//    public CartResponse updateVariant(
//            HttpSession session,
//            @PathVariable Long itemId,
//            @RequestBody @Valid UpdateVariantRequest request) {
//        return cartService.updateVariant(session.getId(), itemId, request.getProductVariantId());
//    }
//
//    @DeleteMapping("/clear")
//    public void clearCart(HttpSession session) {
//        cartService.clearCart(session.getId());
//    }
//
//    @DeleteMapping("/items/{itemId}")
//    public CartResponse removeItem(HttpSession session, @PathVariable Long itemId) {
//        return cartService.removeItem(session.getId(), itemId);
//    }
//
//
//}
