package customerManagement.mapper;

import customerManagement.dto.CustomerRequest;
import customerManagement.dto.CustomerResponse;
import customerManagement.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface CustomerMapper {
    CustomerResponse toDto(Customer customer);
    Customer toEntity(CustomerRequest request);
    void updateCustomer(CustomerRequest request, @MappingTarget Customer customer);

}
