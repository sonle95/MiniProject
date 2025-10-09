package likeUniquloWeb.mapper;

import likeUniquloWeb.dto.response.CartItemResponse;
import likeUniquloWeb.dto.response.CartResponse;
import likeUniquloWeb.entity.Cart;
import likeUniquloWeb.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {
    @Mapping(target = "totalItems", expression = "java(cart.getCartItems() != null ? cart.getCartItems().size() : 0)")
    @Mapping(target = "totalAmount", expression = "java(cart.getCartItems() != null ? cart.getCartItems().stream().map(item -> item.getPrice().multiply(new java.math.BigDecimal(item.getQuantity()))).reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add) : java.math.BigDecimal.ZERO)")
    CartResponse toDto(Cart cart);


    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "unitPrice", source = "price")
    @Mapping(target = "color", source = "productVariant.color")
    @Mapping(target = "size", source = "productVariant.size")
    @Mapping(target = "productVariantId", source = "productVariant.id")
    @Mapping(target = "totalPrice", expression = "java(cartItem.getPrice().multiply(new java.math.BigDecimal(cartItem.getQuantity())))")
    @Mapping(target = "imageUrl", expression = "java(getFirstImageUrl(cartItem))")
    CartItemResponse toCartItemDto(CartItem cartItem);

    default String getFirstImageUrl(CartItem cartItem) {
        if (cartItem.getProductVariant() != null
                && cartItem.getProductVariant().getProduct() != null
                && cartItem.getProductVariant().getProduct().getImages() != null
                && !cartItem.getProductVariant().getProduct().getImages().isEmpty()) {
            return cartItem.getProductVariant().getProduct().getImages().get(0).getUrl();
        }
        return null;
    }
}
