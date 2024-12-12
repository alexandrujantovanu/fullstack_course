package com.amigoscode.alex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AlexApplication {

//    @Bean
//    CommandLineRunner runner(CustomerRepository customerRepository) {
//        return args -> {
//            Faker faker = new Faker(Locale.ENGLISH);
//            Customer customer = new Customer(faker.name().fullName(), faker.internet().emailAddress(), faker.number().numberBetween(1, 100));
//
//            customerRepository.save(customer);
//        };
//    }

    public static void main(String[] args) {
        SpringApplication.run(AlexApplication.class, args);
    }
}
