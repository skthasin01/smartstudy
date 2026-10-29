package com.smartstudy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

/**
 * Smart Study Load and Burnout Prevention System.
 * Main Application Class compatible with Java 8 and Spring Boot 2.7.18.
 */
@SpringBootApplication
@EnableScheduling
public class SmartStudyApplication {

    @PostConstruct
    void init() {
        // Set default timezone to UTC or server local time consistently
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    public static void main(String[] args) {
        SpringApplication.run(SmartStudyApplication.class, args);
    }
}
