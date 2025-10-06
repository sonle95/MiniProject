package likeUniquloWeb.mapper;

import likeUniquloWeb.dto.request.ReviewRequest;
import likeUniquloWeb.dto.response.ReviewResponse;
import likeUniquloWeb.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


import java.util.List;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "product", ignore = true)
    Review toEntity(ReviewRequest request);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userName", source = "user.username")
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    ReviewResponse toDto(Review review);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "product", ignore = true)
    void updateReview(ReviewRequest request, @MappingTarget Review review);

}
