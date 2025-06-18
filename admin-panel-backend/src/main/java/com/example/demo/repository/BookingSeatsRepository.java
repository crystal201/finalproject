package com.example.demo.repository;

import com.example.demo.entity.BookingSeats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingSeatsRepository extends JpaRepository<BookingSeats, Long> {
    List<BookingSeats> findByBookingId(Long bookingId);
}