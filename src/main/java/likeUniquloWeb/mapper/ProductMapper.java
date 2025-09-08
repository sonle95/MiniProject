package likeUniquloWeb.mapper;

import likeUniquloWeb.dto.request.ProductRequest;
import likeUniquloWeb.dto.response.ProductResponse;
import likeUniquloWeb.dto.response.ReviewResponse;
import likeUniquloWeb.dto.response.VariantResponse;
import likeUniquloWeb.entity.Product;
import likeUniquloWeb.entity.Image;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {VariantMapper.class, ReviewMapper.class})
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Product toEntity(ProductRequest request);

    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "urls", expression = "java(mapImagesToUrls(product.getImages()))")
    ProductResponse toDto(Product product);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "images", ignore = true)
    void updateProduct(ProductRequest request, @MappingTarget Product product);

    default List<String> mapImagesToUrls(List<Image> images) {
        if (images == null) return java.util.Collections.emptyList();
        return images.stream().map(Image::getUrl).toList();
    }

//
//    default List<VariantResponse> mapVariants(Product product) {
//        if (product.getProductVariants() == null) return Collections.emptyList();
//        return product.getProductVariants().stream()
//                .map(v -> new VariantResponse(v.getId(), v.getSize(), v.getColor(), v.getPrice()))
//                .collect(Collectors.toList());
//    }
//
//    default List<ReviewResponse> mapReviewsToDto(Product product) {
//        if (product == null || product.getReviews() == null)
//            return Collections.emptyList();
//        return product.getReviews().stream()
//                .map(r ->
//                        new ReviewResponse(r.getRating(), r.getComment()
//                                ,r.getUser().getId(),r.getUser().getUsername()
//                            , r.getCreatedAt()
//                        ))
//                .collect(Collectors.toList());
//    }
}
