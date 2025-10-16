package likeUniquloWeb.mapper;

import likeUniquloWeb.dto.request.UserRequest;
import likeUniquloWeb.dto.request.UserUpdateRequest;
import likeUniquloWeb.dto.response.UserResponse;
import likeUniquloWeb.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;


@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(target = "roles", ignore = true)
    User toEntity(UserRequest request);
    UserResponse toDto(User user);

    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "password", ignore = true)
    void update(UserUpdateRequest request, @MappingTarget User user);

}
