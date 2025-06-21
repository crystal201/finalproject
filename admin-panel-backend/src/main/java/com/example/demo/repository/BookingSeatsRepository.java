package com.example.demo.repository;

import com.example.demo.entity.BookingSeats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingSeatsRepository extends JpaRepository<BookingSeats, Long> {
    List<BookingSeats> findByBookingId(Long bookingId);
    void deleteByBookingId(Long bookingId);
}