package likeUniquloWeb.mapper;

import likeUniquloWeb.dto.request.RegistrationRequest;
import likeUniquloWeb.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RegistrationMapper {

    @Mapping(target = "roles", ignore = true)
    User toEntity(RegistrationRequest request);
}
