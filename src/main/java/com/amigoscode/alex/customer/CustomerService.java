package com.amigoscode.alex.customer;

import ch.qos.logback.core.util.StringUtil;
import com.amigoscode.alex.customer.exception.DuplicateResourceException;
import com.amigoscode.alex.customer.exception.NoDataChangesFoundResources;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {
    private final CustomerDao customerDao;

    public CustomerService(@Qualifier("jdbc") CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    public List<Customer> getAllCustomers() {
        return customerDao.selectAllCustomers();
    }

    public Customer getCustomer(Integer id) {
        return customerDao.selectCustomerById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("customer with id " + id + " was not found"));
    }

    public void addCustomer(CustomerRegistrationRequest
                                    customerRegistrationRequest) {
        String email = customerRegistrationRequest.email();
        if (customerDao.existsCustomerWithEmail(email)) {
            throw new DuplicateResourceException("email already taken");
        }
        customerDao.addCustomer(new Customer(customerRegistrationRequest.name(),
                customerRegistrationRequest.email(),
                customerRegistrationRequest.age())
        );
    }

    public void deleteCustomerById(int id) {
        try {
            customerDao.deleteCustomerById(id);
        } catch (RuntimeException ex) {
            throw new ResourceAccessException("customer with id " + id + " was not found");
        }
    }

    public void updateCustomerById(Integer customerId, CustomerUpdateRequest customerUpdateRequest) {
        Optional<Customer> customer = customerDao.selectCustomerById(customerId);
        if (customer.isPresent() && null != customerUpdateRequest) {
            boolean changePresent = false;
            if (!StringUtil.isNullOrEmpty(customerUpdateRequest.email()) && !customer.get().getEmail().equals(customerUpdateRequest.email())) {
                Optional<Customer> existingCustomerWithEmail = customerDao.selectCustomerByEmail(customerUpdateRequest.email());
                if (existingCustomerWithEmail.isPresent() && !existingCustomerWithEmail.get().getId().equals(customerId)) {
                    throw new DuplicateResourceException("email is already used");
                }

                changePresent = true;
                customer.get().setEmail(customerUpdateRequest.email());
            }
            if (!StringUtil.isNullOrEmpty(customerUpdateRequest.name()) && !customer.get().getName().equals(customerUpdateRequest.name())) {
                changePresent = true;
                customer.get().setName(customerUpdateRequest.name());
            }
            if (null != customerUpdateRequest.age() && !customer.get().getAge().equals(customerUpdateRequest.age())) {
                changePresent = true;
                customer.get().setAge(customerUpdateRequest.age());
            }
            if (changePresent) {
                customerDao.updateCustomer(customerId, customer.get());
            } else {
                throw new NoDataChangesFoundResources("no data changes found");
            }
        } else {
            throw new ResourceAccessException("customer with id " + customerId + " was not found");
        }
    }
}