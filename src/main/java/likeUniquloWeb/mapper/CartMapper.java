package likeUniquloWeb.mapper;

import likeUniquloWeb.dto.response.CartResponse;
import likeUniquloWeb.entity.Cart;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CartMapper {
    CartResponse toDto(Cart cart);
}
