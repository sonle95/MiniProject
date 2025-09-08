package likeUniquloWeb.mapper;

import likeUniquloWeb.dto.request.OrderItemRequest;
import likeUniquloWeb.dto.response.OrderItemResponse;
import likeUniquloWeb.entity.OrderItems;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "price", ignore = true)
    OrderItems itemToEntity(OrderItemRequest request);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name" )
    OrderItemResponse itemToDto(OrderItems orderItems);

    @Mapping(target = "product", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "price", ignore = true)
    void update(OrderItemRequest request, @MappingTarget OrderItems orderItems);


}
