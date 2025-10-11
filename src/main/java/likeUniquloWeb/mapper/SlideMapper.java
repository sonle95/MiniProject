package likeUniquloWeb.mapper;

import likeUniquloWeb.dto.response.SlideResponse;
import likeUniquloWeb.entity.Slide;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SlideMapper {
    @Mapping(target = "imgUrl", expression = "java(toPublicUrl(slide.getImgUrl()))")
    SlideResponse toDto(Slide slide);

    List<SlideResponse> toDtoList(List<Slide> slides);

    default String toPublicUrl(String path) {
        if (path == null) return null;
        return path;
    }
}
