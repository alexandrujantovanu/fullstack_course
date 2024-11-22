package com.amigoscode.alex.customer;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    boolean existsByEmail(String email);

    Optional<Customer> getByEmail(String email);

    @Transactional
    @Modifying
    @Query("UPDATE Customer c set c.name = :name, c.email = :email, c.age = :age where c.id = :id")
    void updateCustomerById(@Param("name") String name, @Param("email") String email, @Param("age") int age, @Param("id") int id);
}
