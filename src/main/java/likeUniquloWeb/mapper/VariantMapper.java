package likeUniquloWeb.mapper;

import likeUniquloWeb.dto.request.VariantRequest;
import likeUniquloWeb.dto.response.VariantResponse;
import likeUniquloWeb.entity.ProductVariant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface VariantMapper {
    @Mapping(target = "price", expression = "java(variant.getPrice())")
    VariantResponse toDto(ProductVariant variant);
    ProductVariant toEntity(VariantRequest request);

    @Mapping(target = "id", ignore = true)
    void updateVariant(VariantRequest request, @MappingTarget ProductVariant variant);

}
