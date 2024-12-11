package com.amigoscode.alex.customer;

import com.amigoscode.alex.customer.exception.DuplicateResourceException;
import com.amigoscode.alex.customer.exception.NoDataChangesFoundResources;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.web.client.ResourceAccessException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {
    private CustomerService underTest;

    @Mock
    private CustomerDao customerDao;
    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new CustomerService(customerDao);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void canGetAllCustomers() {
        //when
        underTest.getAllCustomers();

        //then
        verify(customerDao).selectAllCustomers();
    }

    @Test
    void canGetCustomer() {
        //given
        Long id = 1L;
        Customer customer = new Customer(id, "Alex", "alex@gmail.com", 30);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        //when
        Customer result = underTest.getCustomer(id);

        //then
        assertThat(result).isEqualTo(customer);
    }

    @Test
    void willThrowWhenGetCustomerReturnsEmptyOptional() {
        //given
        Long id = 1L;
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> underTest.getCustomer(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id " + id + " was not found");

    }

    @Test
    void canAddCustomer() {
        //given
        String email = "alex@gmail.com";

        when(customerDao.existsCustomerWithEmail(email)).thenReturn(false);

        String name = "Alex";
        int age = 15;
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(name, email, age);

        //when
        underTest.addCustomer(request);

        //then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).insertCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();
        assertThat(capturedCustomer.getId()).isNull();
        assertThat(capturedCustomer.getName()).isEqualTo(name);
        assertThat(capturedCustomer.getEmail()).isEqualTo(email);
        assertThat(capturedCustomer.getAge()).isEqualTo(age);
    }

    @Test
    void willThrowWhenEmailExistsWhileAddingCustomer() {
        //given
        String email = "alex@gmail.com";

        when(customerDao.existsCustomerWithEmail(email)).thenReturn(true);

        String name = "Alex";
        int age = 15;
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(name, email, age);

        //when
        //then
        assertThatThrownBy(() -> underTest.addCustomer(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("email already taken");
        verify(customerDao, never()).insertCustomer(any());
    }

    @Test
    void deleteCustomerById() {
        //given
        Long id = 1L;

        //when
        underTest.deleteCustomerById(id);

        //then
        verify(customerDao).deleteCustomerById(id);
    }

    @Test
    void willThrowWhenNoCustomerForIdWhileDeleteCustomerById() {
        //given
        Long id = 1L;
        doThrow(ResourceNotFoundException.class).when(customerDao).deleteCustomerById(id);

        //when
        //then
        assertThatThrownBy(() -> underTest.deleteCustomerById(id))
                .isInstanceOf(ResourceAccessException.class)
                .hasMessage("customer with id " + id + " was not found");
    }

    @Test
    void canUpdateCustomerById() {
        //given
        Long id = 1L;
        CustomerUpdateRequest customerUpdateRequest = new CustomerUpdateRequest("Alexander", "alexander@gmail.com", 31);
        Customer customer = new Customer(1L, "Alex", "alex@gmail.com", 30);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        //when
        underTest.updateCustomerById(id, customerUpdateRequest);

        //then
        verify(customerDao).updateCustomer(customer);
        assertThat(customer.getId()).isEqualTo(id);
        assertThat(customer.getName()).isEqualTo("Alexander");
        assertThat(customer.getEmail()).isEqualTo("alexander@gmail.com");
        assertThat(customer.getAge()).isEqualTo(31);
    }

    @Test
    void willUpdateEmailAsItIsUnusedItWhileUpdateCustomerById() {
        //given
        Long id = 1L;
        String requestUpdateEmail = "alexander@gmail.com";
        int requestUpdateAge = 30;
        CustomerUpdateRequest customerUpdateRequest = new CustomerUpdateRequest("Alex", requestUpdateEmail, requestUpdateAge);
        Customer customer = new Customer(1L, "Alex", "alex@gmail.com", 30);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        when(customerDao.selectCustomerByEmail(requestUpdateEmail)).thenReturn(Optional.of(customer));

        //when
        underTest.updateCustomerById(id, customerUpdateRequest);

        //then
        verify(customerDao).updateCustomer(customer);
        assertThat(customer.getEmail()).isEqualTo(requestUpdateEmail);
        assertThat(customer.getName()).isEqualTo("Alex");
        assertThat(customer.getAge()).isEqualTo(30);
    }

    @Test
    void throwExceptionWhenNoChangesFoundWhileUpdateCustomerById() {
        //given
        Long id = 1L;
        CustomerUpdateRequest customerUpdateRequest = new CustomerUpdateRequest("Alex", "alex@gmail.com", 30);
        Customer customer = new Customer(1L, "Alex", "alex@gmail.com", 30);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        //when
        //then
        assertThatThrownBy(() -> underTest.updateCustomerById(id, customerUpdateRequest))
                .isInstanceOf(NoDataChangesFoundResources.class)
                .hasMessage("no data changes found");
    }

    @Test
    void throwExceptionWhenCustomerNotFoundWhileUpdateCustomerById() {
        //given
        Long id = 1L;
        CustomerUpdateRequest customerUpdateRequest = new CustomerUpdateRequest("Alex", "alex@gmail.com", 30);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> underTest.updateCustomerById(id, customerUpdateRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id " + id + " was not found");
    }

    @Test
    void willDoNothingWhenCustomerUpdateRequestIsNullWhileUpdateCustomerById() {
        //given
        Long id = 1L;
        Customer customer = new Customer(id, "Alex", "alex@gmail.com", 30);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        //when
        underTest.updateCustomerById(id, null);

        //then
        verify(customerDao, never()).selectCustomerByEmail(anyString());
        verify(customerDao, never()).updateCustomer(any(Customer.class));
    }

    @Test
    void willThrowExceptionWhenUpdatingWithExistingEmailWhileUpdateCustomerById() {
        //given
        Long id = 1L;
        String requestUpdateEmail = "bsoad@gmail.com";
        String requestUpdateName = "Alexander";
        CustomerUpdateRequest customerUpdateRequest = new CustomerUpdateRequest(requestUpdateName, requestUpdateEmail, 31);
        Customer customer = new Customer(1L, "Alex", "alex@gmail.com", 30);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        Customer existingCustomerForEmail = new Customer(2L, "Bob Soad", requestUpdateEmail, 30);
        when(customerDao.selectCustomerByEmail(requestUpdateEmail)).thenReturn(Optional.of(existingCustomerForEmail));

        //when
        //then
        assertThatThrownBy(() -> underTest.updateCustomerById(id, customerUpdateRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("email is already used");

    }

    @Test
    void willThrowExceptionAsRequestUpdateValuesAreNullWhileUpdateCustomerById() {
        //given
        Long id = 1L;
        String sameCustomerEmail = "alex@gmail.com";
        String requestUpdateName = "Alexander";
        CustomerUpdateRequest customerUpdateRequest = new CustomerUpdateRequest(null, null, null);

        Customer customer = new Customer(1L, "Alex", sameCustomerEmail, 30);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        //when
        //then
        assertThatThrownBy(() -> underTest.updateCustomerById(id, customerUpdateRequest))
                .isInstanceOf(NoDataChangesFoundResources.class)
                .hasMessage("no data changes found");
    }
}