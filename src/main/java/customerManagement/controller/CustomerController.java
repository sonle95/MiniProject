package customerManagement.controller;

import customerManagement.dto.CustomerRequest;
import customerManagement.dto.CustomerResponse;
import customerManagement.service.CustomerService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
@CrossOrigin
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomerController {
    CustomerService customerService;

    @PostMapping
    public CustomerResponse createCustomer(@RequestBody CustomerRequest request){
        return  customerService.createCustomer(request);
    }

    @GetMapping
    public List<CustomerResponse> getAllCustomers(){
        return customerService.getAllCustomers();
    }

    @GetMapping("/{id}")
    public CustomerResponse findById(@PathVariable Long id){
        return customerService.findById(id);
    }

    @PutMapping("/{id}")
    public CustomerResponse updateCustomer(@PathVariable Long id, @RequestBody CustomerRequest request){
        return customerService.updateCustomer(id, request);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Long id){
        customerService.deleteById(id);
    }

}
