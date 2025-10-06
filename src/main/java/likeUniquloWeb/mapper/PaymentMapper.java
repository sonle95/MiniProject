package likeUniquloWeb.mapper;

import likeUniquloWeb.dto.request.PaymentRequest;
import likeUniquloWeb.dto.response.PaymentResponse;
import likeUniquloWeb.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class})
public interface PaymentMapper {
    @Mapping(target = "orderId", source = "order.id")
    PaymentResponse toDto(Payment payment);

    @Mapping(target = "order", ignore = true)
    Payment toEntity(PaymentRequest request);
}
