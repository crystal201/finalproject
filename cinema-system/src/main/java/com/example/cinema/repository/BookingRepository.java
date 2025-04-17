package com.example.cinema.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.cinema.entity.Booking;

public interface BookingRepository extends JpaRepository<Booking, Long> { 
    List<Booking> findByUserId(String userId);
}