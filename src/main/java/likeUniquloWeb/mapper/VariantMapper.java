package likeUniquloWeb.mapper;

import likeUniquloWeb.dto.request.VariantRequest;
import likeUniquloWeb.dto.response.VariantResponse;
import likeUniquloWeb.entity.ProductVariant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface VariantMapper {

    VariantResponse toDto(ProductVariant variant);

    @Mapping(target = "stocks", ignore = true)
    @Mapping(target = "product", ignore = true)
    ProductVariant toEntity(VariantRequest request);

    @Mapping(target = "stocks", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updateVariant(VariantRequest request, @MappingTarget ProductVariant variant);

}
