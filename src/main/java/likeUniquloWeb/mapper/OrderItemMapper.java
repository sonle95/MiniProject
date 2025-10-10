package likeUniquloWeb.mapper;

import likeUniquloWeb.dto.request.OrderItemRequest;
import likeUniquloWeb.dto.response.OrderItemResponse;
import likeUniquloWeb.entity.OrderItems;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring", uses = {VariantMapper.class})
public interface OrderItemMapper {
    @Mapping(target = "id", ignore = true)
    OrderItems itemToEntity(OrderItemRequest request);

    @Mapping(target = "productName", source = "productVariant.product.name" )
    @Mapping(target = "productVariantId", source = "productVariant.id")
    @Mapping(target = "productSku", source = "productVariant.product.stockKeepingUnit")
    @Mapping(target = "size", source = "productVariant.size")
    @Mapping(target = "color", source = "productVariant.color")
    OrderItemResponse itemToDto(OrderItems orderItems);

    @Mapping(target = "id", ignore = true)
    void update(OrderItemRequest request, @MappingTarget OrderItems orderItems);


}
