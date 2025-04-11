package com.example.cinema;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.example.cinema")
public class CinemaSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(CinemaSystemApplication.class, args);
        System.out.println("Cinema System is running...");
    }
}
