package com.nspace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class JavaBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(JavaBackendApplication.class, args);
    }

    @GetMapping("/")
    public String home() {
        return "NSPACE Java Backend is running!";
    }

    @GetMapping("/health")
    public String health() {
        return "{\"status\": \"healthy\", \"service\": \"java-backend\"}";
    }

    @GetMapping("/api/termine")
    public String getTermine() {
        return "{\"message\": \"Java Termine API\", \"termine\": []}";
    }
}
