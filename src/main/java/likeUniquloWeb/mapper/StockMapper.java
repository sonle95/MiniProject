package likeUniquloWeb.mapper;

import likeUniquloWeb.dto.request.StockRequest;
import likeUniquloWeb.dto.response.StockResponse;
import likeUniquloWeb.entity.Stock;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;


@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StockMapper {


    @Mapping(target = "lastUpdated", ignore = true)
    Stock toEntity(StockRequest request);

    @Mapping(target = "productVariantId", source = "productVariant.id")
    @Mapping(target = "quantity", source = "quantity")
    @Mapping(target = "productName", source = "productVariant.product.name")
    StockResponse toDto(Stock stock);
    void update(StockRequest request, @MappingTarget Stock stock);

}
