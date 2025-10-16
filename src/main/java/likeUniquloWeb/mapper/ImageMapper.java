package likeUniquloWeb.mapper;

import likeUniquloWeb.dto.response.ImageResponse;
import likeUniquloWeb.entity.Image;
import org.mapstruct.MapMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ImageMapper {

    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "productId", source = "product.id")
    ImageResponse imageToDto(Image image);


    List<ImageResponse> imgToDto(List<Image> images);
    List<Image> imgToEntity(List<ImageResponse> imageResponses);
}
