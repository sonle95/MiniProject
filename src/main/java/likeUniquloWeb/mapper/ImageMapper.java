package likeUniquloWeb.mapper;

import likeUniquloWeb.dto.response.ImageResponse;
import likeUniquloWeb.entity.Image;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ImageMapper {
    List<ImageResponse> imgToDto(List<Image> images);
    List<Image> imgToEntity(List<ImageResponse> imageResponses);
}
