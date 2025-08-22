package likeUniquloWeb.mapper;

import likeUniquloWeb.dto.request.OrderRequest;
import likeUniquloWeb.dto.response.OrderResponse;
import likeUniquloWeb.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring", uses = {OrderItemMapper.class})
public interface OrderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "coupon", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createAt", ignore = true)
    @Mapping(target = "updateAt", ignore = true)
    @Mapping(target = "totalAmount", ignore = true)
    @Mapping(target = "orderItems", source = "orderItems")
    Order orderToEntity(OrderRequest request);

    @Mapping(target = "orderItemResponses", source = "orderItems")
    OrderResponse orderToDto(Order order);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "coupon", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createAt", ignore = true)
    @Mapping(target = "updateAt", ignore = true)
    @Mapping(target = "totalAmount", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    void updateOrder(OrderRequest request, @MappingTarget Order order);
}
