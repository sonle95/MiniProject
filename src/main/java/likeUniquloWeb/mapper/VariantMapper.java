package likeUniquloWeb.mapper;

import likeUniquloWeb.dto.request.VariantRequest;
import likeUniquloWeb.dto.response.VariantResponse;
import likeUniquloWeb.entity.ProductVariant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {StockMapper.class})
public interface VariantMapper {

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    VariantResponse toDto(ProductVariant variant);

    @Mapping(target = "stock.productVariant", ignore = true)
    ProductVariant toEntity(VariantRequest request);


    void updateVariant(VariantRequest request, @MappingTarget ProductVariant variant);

}
