package likeUniquloWeb.mapper;

import likeUniquloWeb.dto.response.ImageResponse;
import likeUniquloWeb.entity.Image;
import org.mapstruct.MapMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ImageMapper {

    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "productId", source = "product.id")
    ImageResponse imageToDto(Image image);


    List<ImageResponse> imgToDto(List<Image> images);
    List<Image> imgToEntity(List<ImageResponse> imageResponses);
}
