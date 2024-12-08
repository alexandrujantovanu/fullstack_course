package com.amigoscode.alex.customer;

import com.amigoscode.alex.AbstractTestContainers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CustomerJDBCDataAccessServiceTest extends AbstractTestContainers {

    private final CustomerRowMapper rowMapper = new CustomerRowMapper();
    private CustomerJDBCDataAccessService underTest;

    @BeforeEach
    void setUp() {
        underTest = new CustomerJDBCDataAccessService(getJdbcTemplate(), rowMapper);
    }

    @Test
    void selectAllCustomers() {
        //given
        Customer customer = new Customer(FAKER.name().fullName(),
                FAKER.internet().emailAddress() + "-" + UUID.randomUUID(),
                20);
        underTest.insertCustomer(customer);

        //when
        List<Customer> actual = underTest.selectAllCustomers();

        //then
        assertThat(actual).isNotEmpty();
    }

    @Test
    void selectCustomerById() {
        //given
        String email = FAKER.internet().emailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(FAKER.name().fullName(),
                email,
                20);
        underTest.insertCustomer(customer);

        var id = underTest.selectAllCustomers().stream()
                .filter(c -> email.equals(c.getEmail()))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        //when
        Optional<Customer> actual = underTest.selectCustomerById(id);


        //then
        assertThat(actual).isPresent().hasValueSatisfying(c -> {
                    assertThat(c.getId()).isEqualTo(id);
                    assertThat(c.getName()).isEqualTo(customer.getName());
                    assertThat(c.getEmail()).isEqualTo(customer.getEmail());
                    assertThat(c.getAge()).isEqualTo(customer.getAge());
                }
        );
    }

    @Test
    void selectCustomerByIdWillReturnEmptyWhenCustomerWithIdNotPresent() {
        //given
        long id = -1;

        //when
        Optional<Customer> actual = underTest.selectCustomerById(id);

        //then
        assertThat(actual).isEmpty();
    }

    @Test
    void selectCustomerByEmail() {
        //given
        String email = FAKER.internet().emailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(FAKER.name().fullName(),
                email,
                20);
        underTest.insertCustomer(customer);

        //when
        Optional<Customer> actual = underTest.selectCustomerByEmail(email);


        //then
        assertThat(actual).isPresent().hasValueSatisfying(c -> {
                    assertThat(c.getName()).isEqualTo(customer.getName());
                    assertThat(c.getEmail()).isEqualTo(customer.getEmail());
                    assertThat(c.getAge()).isEqualTo(customer.getAge());
                }
        );
    }

    @Test
    void selectCustomerByEmailWillReturnEmptyWhenCustomerWithEmailNotPresent() {
        //given
        String email = FAKER.internet().emailAddress() + "-" + UUID.randomUUID();

        //when
        Optional<Customer> actual = underTest.selectCustomerByEmail(email);


        //then
        assertThat(actual).isEmpty();
    }

    @Test
    void insertCustomer() {
        //given
        String email = FAKER.internet().emailAddress() + "-" + UUID.randomUUID();
        Customer newCustomer = new Customer(FAKER.name().fullName(),
                email,
                20);

        //when
        underTest.insertCustomer(newCustomer);

        //then
        Optional<Customer> customer = underTest.selectCustomerByEmail(email);
        assertThat(customer).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getName()).isEqualTo(newCustomer.getName());
            assertThat(c.getEmail()).isEqualTo(newCustomer.getEmail());
            assertThat(c.getAge()).isEqualTo(newCustomer.getAge());
        });
    }

    @Test
    void existsCustomerWithEmail() {
        //given
        String email = FAKER.internet().emailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(FAKER.name().fullName(),
                email,
                20);

        underTest.insertCustomer(customer);

        //when
        boolean result = underTest.existsCustomerWithEmail(email);

        //then
        assertThat(result).isTrue();
    }

    @Test
    void customerNotFoundExistsCustomerWithEmail() {
        //given
        String email = FAKER.internet().emailAddress() + "-" + UUID.randomUUID();

        //when
        boolean result = underTest.existsCustomerWithEmail(email);

        //then
        assertThat(result).isFalse();
    }

    @Test
    void deleteCustomerById() {
        //given
        String email = FAKER.internet().emailAddress() + "-" + UUID.randomUUID();
        Customer newCustomer = new Customer(FAKER.name().fullName(),
                email,
                20);
        underTest.insertCustomer(newCustomer);

        long id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(newCustomer.getEmail()))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        //when
        underTest.deleteCustomerById(id);

        //then
        Optional<Customer> deletedCustomer = underTest.selectCustomerByEmail(email);
        assertThat(deletedCustomer).isNotPresent();
    }

    @Test
    void idNotPresentDeleteCustomerById() {
        //given

        //when
        underTest.deleteCustomerById(-1L);

        //then
    }

    @Test
    void updateCustomerNameAndAge() {
        String email = FAKER.internet().emailAddress() + "-" + UUID.randomUUID();
        Customer newCustomer = new Customer(FAKER.name().fullName(),
                email,
                20);
        underTest.insertCustomer(newCustomer);
        Optional<Customer> insertedCustomer = underTest.selectCustomerByEmail(email);
        assertThat(insertedCustomer).isPresent();

        Customer customerUpdated = new Customer(insertedCustomer.get().getId(), FAKER.name().fullName() + " " + UUID.randomUUID(), insertedCustomer.get().getEmail(), FAKER.number().randomDigit());
        //when
        underTest.updateCustomer(customerUpdated);

        //then
        Optional<Customer> updatedCustomer = underTest.selectCustomerByEmail(email);
        assertThat(updatedCustomer).isPresent().hasValueSatisfying(uc -> {
            assertThat(uc.getId()).isEqualTo(insertedCustomer.get().getId());
            assertThat(uc.getName()).isNotEqualTo(insertedCustomer.get().getName());
            assertThat(uc.getEmail()).isEqualTo(insertedCustomer.get().getEmail());
            assertThat(uc.getAge()).isNotEqualTo(insertedCustomer.get().getAge());
        });
    }

    @Test
    void updateCustomerWillDoNothingWhenCustomerMissing() {
        //given
        String email = FAKER.internet().emailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(-1L, FAKER.name().fullName(),
                email,
                20);
        //when
        underTest.updateCustomer(customer);

        //then
        assertThat(underTest.selectCustomerByEmail(email)).isNotPresent();
    }

    @Test
    void emailIsAlreadyInUseWhenEmailAlreadyInUseExpectException() {
        int age = 20;
        String email1 = FAKER.internet().emailAddress() + "-" + UUID.randomUUID();
        String alreadyPresentEmail = FAKER.internet().emailAddress() + "-" + UUID.randomUUID();
        Customer newCustomer1 = new Customer(FAKER.name().fullName(),
                email1,
                age);
        Customer newCustomer2 = new Customer(FAKER.name().fullName(),
                alreadyPresentEmail,
                age);
        underTest.insertCustomer(newCustomer1);
        underTest.insertCustomer(newCustomer2);

        Optional<Customer> insertedCustomer1 = underTest.selectCustomerByEmail(email1);
        assertThat(insertedCustomer1).isPresent();

        Customer customerToUpdate = new Customer(insertedCustomer1.get().getId(), FAKER.name().fullName(), alreadyPresentEmail, age);
        //when
        assertThrows(RuntimeException.class, () -> {
            underTest.updateCustomer(customerToUpdate);
        });

    }
}