package com.amigoscode.alex;

import com.amigoscode.alex.customer.Customer;
import com.amigoscode.alex.customer.CustomerRegistrationRequest;
import com.amigoscode.alex.customer.CustomerService;
import com.amigoscode.alex.customer.CustomerUpdateRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public List<Customer> getCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping("/{id}")
    public Customer getCustomer(@PathVariable int id) {
        return customerService.getCustomer(id);
    }

    @PostMapping
    public void registerCustomer(@RequestBody CustomerRegistrationRequest customerRegistrationRequest) {
        customerService.addCustomer(customerRegistrationRequest);
    }

    @DeleteMapping("/{id}")
    public void deleteCustomer(@PathVariable Integer id) {
        customerService.deleteCustomerById(id);
    }

    @PutMapping("/{customerId}")
    public void updateCustomer(@PathVariable Integer customerId, @RequestBody CustomerUpdateRequest customerUpdateRequest) {
        customerService.updateCustomerById(customerId, customerUpdateRequest);

    }
}
