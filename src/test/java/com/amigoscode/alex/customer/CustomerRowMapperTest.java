package com.amigoscode.alex.customer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class CustomerRowMapperTest {
    private CustomerRowMapper rowMapper;
    @Mock
    private AutoCloseable autoCloseable;
    @Mock
    private ResultSet resultSet;


    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        rowMapper = new CustomerRowMapper();
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void willMapCustomerOnMapRow() throws SQLException {
        //given
        long expectedId = 1L;
        String expectedName = "Ron";
        String expectedEmail = "Wizzly";
        int expectedAge = 18;
        when(resultSet.getLong("id")).thenReturn(expectedId);
        when(resultSet.getString("name")).thenReturn(expectedName);
        when(resultSet.getString("email")).thenReturn(expectedEmail);
        when(resultSet.getInt("age")).thenReturn(expectedAge);

        //when
        Customer customer = rowMapper.mapRow(resultSet, 1);

        //then
        assertThat(customer).isNotNull();
        assertThat(customer.getId()).isEqualTo(expectedId);
        assertThat(customer.getName()).isEqualTo(expectedName);
        assertThat(customer.getEmail()).isEqualTo(expectedEmail);
        assertThat(customer.getAge()).isEqualTo(expectedAge);
    }
}