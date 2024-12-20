package com.amigoscode.alex.customer;

import java.util.List;
import java.util.Optional;

public interface CustomerDao {

    List<Customer> selectAllCustomers();

    Optional<Customer> selectCustomerById(Integer id);
    Optional<Customer> selectCustomerByEmail(String email);

    void addCustomer(Customer customer);

    boolean existsCustomerWithEmail(String email);

    void deleteCustomerById(Integer id);

    void updateCustomer(Integer id, Customer customer);


}
