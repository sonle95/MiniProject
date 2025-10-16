package likeUniquloWeb.mapper;

import likeUniquloWeb.dto.request.OrderRequest;
import likeUniquloWeb.dto.response.OrderResponse;
import likeUniquloWeb.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;


@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {OrderItemMapper.class})
public interface OrderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "coupon", ignore = true)
    @Mapping(target = "address", ignore = true)
    Order orderToEntity(OrderRequest request);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "paymentStatus", target = "paymentStatus")
    @Mapping(source = "paymentMethod", target = "paymentMethod")
    @Mapping(source = "coupon.code", target = "couponCode")
    OrderResponse orderToDto(Order order);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "coupon", ignore = true)
    void updateOrder(OrderRequest request, @MappingTarget Order order);
}
