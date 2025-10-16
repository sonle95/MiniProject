package likeUniquloWeb.mapper;

import likeUniquloWeb.dto.request.AddressRequest;
import likeUniquloWeb.dto.response.AddressResponse;
import likeUniquloWeb.entity.Address;
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
public interface AddressMapper {

    @Mapping(target = "user", ignore = true)
    Address toEntity(AddressRequest request);


    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "email", source = "user.email")
    AddressResponse toDto(Address address);

    @Mapping(target = "user", ignore = true)
    void updateAddress(AddressRequest request,@MappingTarget Address address);
}
