package com.highway.tolling.iot.simulator.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Simulator Configuration
 * Provides beans needed for the integrated IoT simulator
 */
@Configuration
public class SimulatorConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
