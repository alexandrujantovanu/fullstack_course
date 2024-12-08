package com.amigoscode.alex.customer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

class CustomerJPADataAccessServiceTest {

    private CustomerJPADataAccessService underTest;
    private AutoCloseable autoClosable;

    @Mock
    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        autoClosable = MockitoAnnotations.openMocks(this);
        underTest = new CustomerJPADataAccessService(customerRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoClosable.close();
    }


    @Test
    void selectAllCustomers() {
        //when
        underTest.selectAllCustomers();

        //then
        verify(customerRepository).findAll();
    }

    @Test
    void selectCustomerById() {
        //given
        Long id = 1L;

        //when
        underTest.selectCustomerById(id);

        //then
        verify(customerRepository).findById(id);
    }

    @Test
    void insertCustomer() {
        //given
        Customer customer = new Customer();

        //when
        underTest.insertCustomer(customer);

        //then
        verify(customerRepository).save(customer);
    }

    @Test
    void existsCustomerWithEmail() {
        //given
        String emailAddress = "test@test.com";

        //when
        underTest.existsCustomerWithEmail(emailAddress);

        //then
        verify(customerRepository).existsByEmail(emailAddress);
    }

    @Test
    void deleteCustomerById() {
        //given
        Long id = 1L;

        //when
        underTest.deleteCustomerById(id);

        //then
        verify(customerRepository).deleteById(id);
    }

    @Test
    void updateCustomer() {
        //given
        Customer customer = new Customer(1L, "testName", "test@email.com", 10);

        //when
        underTest.updateCustomer(customer);

        //then
        verify(customerRepository).updateCustomerById(1L, "testName", "test@email.com", 10);
    }

    @Test
    void selectCustomerByEmail() {
        //given
        String emailAddress = "test@test.com";

        //when
        underTest.selectCustomerByEmail(emailAddress);

        //then
        verify(customerRepository).getByEmail(emailAddress);
    }
}