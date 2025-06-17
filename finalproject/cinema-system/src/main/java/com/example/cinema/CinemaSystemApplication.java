package com.example.cinema;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.example.cinema")
@EnableScheduling
public class CinemaSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(CinemaSystemApplication.class, args);
        System.out.println("Cinema System is running...");
    }
}
