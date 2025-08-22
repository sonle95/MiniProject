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

    Review toEntity(ReviewRequest request);
    ReviewResponse toDto(Review review);

    @Mapping(target = "id", ignore = true)
    void updateReview(ReviewRequest request, @MappingTarget Review review);

}
