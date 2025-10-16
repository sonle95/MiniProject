package likeUniquloWeb.mapper;

import likeUniquloWeb.dto.request.VariantRequest;
import likeUniquloWeb.dto.response.VariantResponse;
import likeUniquloWeb.entity.ProductVariant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {StockMapper.class})
public interface VariantMapper {

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    VariantResponse toDto(ProductVariant variant);

    @Mapping(target = "product", ignore = true )
    @Mapping(target = "stock.productVariant", ignore = true)
    ProductVariant toEntity(VariantRequest request);


    void updateVariant(VariantRequest request, @MappingTarget ProductVariant variant);

}
