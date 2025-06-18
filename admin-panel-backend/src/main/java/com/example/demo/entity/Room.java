package com.example.demo.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.Table;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roomName;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}