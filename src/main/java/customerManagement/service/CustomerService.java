package customerManagement.service;

import customerManagement.dto.CustomerRequest;
import customerManagement.dto.CustomerResponse;
import customerManagement.entity.Customer;
import customerManagement.mapper.CustomerMapper;
import customerManagement.repository.CustomerRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class CustomerService {
    CustomerRepository customerRepository;
    CustomerMapper customerMapper;

    public CustomerResponse createCustomer(CustomerRequest request){
        Customer customer = customerMapper.toEntity(request);
        customerRepository.save(customer);
        CustomerResponse customerResponse = customerMapper.toDto(customer);
        return customerResponse;
    }

    public List<CustomerResponse> getAllCustomers(){
       return  customerRepository.findAll().stream().map
                (customerMapper::toDto).toList();
    }

    public CustomerResponse findById(Long id){
        Customer customer = customerRepository.findById(id).
                orElseThrow(()-> new RuntimeException("customer not found"));
        return customerMapper.toDto(customer);
    }

    public CustomerResponse updateCustomer(Long id, CustomerRequest request){
        Customer customer = customerRepository.findById(id).
                orElseThrow(()-> new RuntimeException("customer not found"));
        customerMapper.updateCustomer(request, customer);
        customerRepository.save(customer);
        return customerMapper.toDto(customer);
    }

    public void deleteById(Long id){
        customerRepository.deleteById(id);
    }
}
