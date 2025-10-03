package likeUniquloWeb.mapper;

import likeUniquloWeb.dto.request.PermissionRequest;
import likeUniquloWeb.dto.response.PermissionResponse;
import likeUniquloWeb.entity.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toEntity(PermissionRequest request);
    PermissionResponse toDto(Permission permission);
}
