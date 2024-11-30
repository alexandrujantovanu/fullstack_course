package com.amigoscode.alex.customer;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("jdbc")
public class CustomerJDBCDataAccessService implements CustomerDao {

    private final JdbcTemplate jdbcTemplate;
    private final CustomerRowMapper customerRowMapper;

    public CustomerJDBCDataAccessService(JdbcTemplate jdbcTemplate, CustomerRowMapper customerRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.customerRowMapper = customerRowMapper;
    }

    @Override
    public List<Customer> selectAllCustomers() {
        String sql = """
                SELECT id, name, email, age
                FROM customer;
                """;


        return jdbcTemplate.query(sql, customerRowMapper);
    }

    @Override
    public Optional<Customer> selectCustomerById(Integer id) {
        return jdbcTemplate.query("""
                        select id, name, email, age
                        from customer
                        where id = ?
                        """, customerRowMapper, id)
                .stream()
                .findFirst();
    }

    @Override
    public Optional<Customer> selectCustomerByEmail(String email) {
        return jdbcTemplate.query("""
                        SELECT id, name, email, age
                        FROM customer
                        WHERE email = ?
                        """, customerRowMapper, email)
                .stream()
                .findFirst();
    }

    @Override
    public void insertCustomer(Customer customer) {
        int update = jdbcTemplate.update("""
                INSERT INTO customer(name, email, age)
                VALUES(?, ?, ?);
                """, customer.getName(), customer.getEmail(), customer.getAge());
        System.out.println("jdbcTemplate.update = " + update);
    }

    @Override
    public boolean existsCustomerWithEmail(String email) {
        String query = """
                SELECT count(id) FROM customer WHERE email=?
                """;
        Integer count = jdbcTemplate.queryForObject(query, Integer.class, email);
        return count != null && count > 0;
    }

    @Override
    public void deleteCustomerById(Integer id) {
        String query = """
                DELETE
                FROM customer
                WHERE id = ?
                """;
        int delete = jdbcTemplate.update(query, id);
    }

    @Override
    public void updateCustomer(Customer customer) {
        Optional<Customer> existingCustomer = selectCustomerById(customer.getId().intValue());
        if (existingCustomer.isEmpty()) {
            return;
        }

        //CASE 3. Customer changed everything and this is ok
        String query = """
                UPDATE customer
                SET name=?, email=?, age=?
                WHERE id=?
                """;
        jdbcTemplate.update(query, customer.getName(), customer.getEmail(), customer.getAge(), customer.getId().intValue());
    }
}
