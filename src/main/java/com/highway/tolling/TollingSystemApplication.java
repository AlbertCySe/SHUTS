package com.highway.tolling;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main Application Class for Smart Highway Usage-Based Tolling System
 * 
 * This is the entry point of the Spring Boot application.
 * The @SpringBootApplication annotation enables auto-configuration,
 * component scanning, and configuration.
 * The @EnableScheduling annotation enables scheduled tasks.
 */
@SpringBootApplication
@EnableScheduling
public class TollingSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(TollingSystemApplication.class, args);
        System.out.println("Smart Highway Tolling System Started Successfully!");
    }
}
