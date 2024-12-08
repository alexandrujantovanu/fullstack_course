package com.amigoscode.alex.customer;

import com.amigoscode.alex.AbstractTestContainers;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomerRepositoryTest extends AbstractTestContainers {

    @Autowired
    private CustomerRepository underTest;

    @Autowired
    private EntityManager em;

    @BeforeEach
    void setUp() {
        underTest.deleteAll();
    }

    @Test
    void existsByEmail() {
        //given
        Customer newCustomer = new Customer(FAKER.name().fullName(),
                FAKER.internet().emailAddress() + "-" + UUID.randomUUID(),
                20);
        var email = underTest.save(newCustomer).getEmail();

        //when
        boolean result = underTest.existsByEmail(email);

        //then
        assertThat(result).isTrue();
    }

    @Test
    void getByEmail() {
        //given
        Customer newCustomer = new Customer(FAKER.name().fullName(),
                FAKER.internet().emailAddress() + "-" + UUID.randomUUID(),
                20);
        var email = underTest.save(newCustomer).getEmail();

        //when
        var result = underTest.getByEmail(email);

        //then
        assertThat(result).isNotEmpty();
    }

    @Test
    void updateCustomerById() {
        //given
        String name = FAKER.name().fullName();
        String email = FAKER.internet().emailAddress() + "-" + UUID.randomUUID();
        int age = 20;
        final Customer newCustomer = new Customer(
                name,
                email,
                age);
        var savedCustomer = underTest.save(newCustomer);

        //when
        underTest.updateCustomerById(savedCustomer.getId(), "Ron", email, 10);
        // When you perform a @Modifying query in JPA, it directly updates the database, bypassing the persistence context.
        // It seems that in the UnitTest the instance is not refreshed automatically, that's why I refresh it in here.
        em.refresh(savedCustomer);

        //then
        Optional<Customer> updatedCustomer = underTest.findById(savedCustomer.getId());
        assertThat(updatedCustomer).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getName()).isEqualTo("Ron");
            assertThat(c.getEmail()).isEqualTo(savedCustomer.getEmail());
            assertThat(c.getAge()).isEqualTo(10);
        });
    }
}