package likeUniquloWeb.mapper;

import likeUniquloWeb.dto.request.RoleRequest;
import likeUniquloWeb.dto.response.RoleResponse;
import likeUniquloWeb.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role toEntity(RoleRequest request);
    RoleResponse toDto(Role role);

    @Mapping(target = "permissions", ignore = true)
    void update(RoleRequest request, @MappingTarget Role role);
}
