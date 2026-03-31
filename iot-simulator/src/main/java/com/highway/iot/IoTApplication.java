package com.highway.iot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.highway.iot", "com.highway.simulator"})
@EnableJpaRepositories(basePackages = "com.highway.simulator.repository")
@EntityScan(basePackages = "com.highway.simulator.entity")
@EnableScheduling
public class IoTApplication {

    public static void main(String[] args) {
        SpringApplication.run(IoTApplication.class, args);
    }
}
