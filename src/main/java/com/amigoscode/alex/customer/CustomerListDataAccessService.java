package com.amigoscode.alex.customer;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository("list")
public class CustomerListDataAccessService implements CustomerDao {
    static final List<Customer> customers;

    static {
        customers = new ArrayList<>();
        final Customer alex = new Customer(1L, "Alex", "alex@gmail.com", 21);
        final Customer jamila = new Customer(2L, "Jamila", "jamila@gmail.com", 20);
        customers.add(alex);
        customers.add(jamila);
    }

    @Override
    public List<Customer> selectAllCustomers() {
        return customers;
    }

    @Override
    public Optional<Customer> selectCustomerById(Integer id) {
        return customers.stream().filter(customer -> id.equals(customer.getId()))
                .findFirst();
    }

    @Override
    public void addCustomer(Customer customer) {
        customers.add(customer);
    }

    @Override
    public boolean existsCustomerWithEmail(String email) {
        return customers.stream().anyMatch(customer -> customer.getEmail().equals(email));
    }

    @Override
    public void deleteCustomerById(Integer id) {
        customers.removeIf(customer -> id.equals(customer.getId()));
    }

    @Override
    public void updateCustomer(Integer id, Customer customer) {
        Optional<Customer> oldCustomer = customers.stream().filter(cust -> id.equals(cust.getId())).findAny();
        if (oldCustomer.isPresent()) {
            oldCustomer.get().setName(customer.getName());
            oldCustomer.get().setEmail(customer.getEmail());
            oldCustomer.get().setAge(customer.getAge());
        }
    }

    @Override
    public Optional<Customer> selectCustomerByEmail(String email) {
        return customers.stream().filter(customer -> email.equals(customer.getEmail())).findAny();
    }
}
