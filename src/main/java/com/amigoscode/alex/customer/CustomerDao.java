package com.amigoscode.alex.customer;

import java.util.List;
import java.util.Optional;

public interface CustomerDao {

    List<Customer> selectAllCustomers();

    Optional<Customer> selectCustomerById(Long id);

    Optional<Customer> selectCustomerByEmail(String email);

    void insertCustomer(Customer customer);

    boolean existsCustomerWithEmail(String email);

    void deleteCustomerById(Long id);

    void updateCustomer(Customer customer);
}
