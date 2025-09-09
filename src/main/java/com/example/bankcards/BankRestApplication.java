package com.example.bankcards;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Bank Cards Management REST API application.
 *
 * <p>This class bootstraps the Spring Boot application,
 * starting the embedded server
 * and initializing all components, such as controllers,
 * services, and repositories.
 *
 * <p>Run this application to launch the REST API for
 * managing bank cards, user authentication,
 * and card operations like creation, blocking,
 * activation, deletion, and transfers.
 */
@SpringBootApplication
public class BankRestApplication {

    /**
     * Main method used to launch the Spring Boot application.
     *
     * @param args command-line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(BankRestApplication.class, args);
    }
}
