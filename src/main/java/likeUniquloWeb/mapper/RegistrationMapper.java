package likeUniquloWeb.mapper;

import likeUniquloWeb.dto.request.RegistrationRequest;
import likeUniquloWeb.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RegistrationMapper {

    @Mapping(target = "roles", ignore = true)
    User toEntity(RegistrationRequest request);
}
