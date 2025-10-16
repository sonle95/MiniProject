package likeUniquloWeb.mapper;

import likeUniquloWeb.dto.request.ProductRequest;
import likeUniquloWeb.dto.request.ProductUpdateRequest;
import likeUniquloWeb.dto.response.ProductResponse;
import likeUniquloWeb.entity.Product;
import likeUniquloWeb.entity.Image;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {VariantMapper.class, ReviewMapper.class})
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "images", ignore = true)
    Product toEntity(ProductRequest request);

    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "urls", expression = "java(mapImagesToUrls(product.getImages()))")
    @Mapping(target = "productVariants", source = "productVariants")
    @Mapping(target = "reviews", source = "reviews")
    ProductResponse toDto(Product product);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    void updateProduct(ProductUpdateRequest request, @MappingTarget Product product);

    default List<String> mapImagesToUrls(List<Image> images) {
        if (images == null) return java.util.Collections.emptyList();
        return images.stream().map(Image::getUrl).toList();
    }
}
