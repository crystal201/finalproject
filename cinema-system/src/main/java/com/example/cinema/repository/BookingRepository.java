package com.example.cinema.repository;

import com.example.cinema.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserIdAndStatus(String userId, Booking.Status status);
    List<Booking> findByUserId(String userId);

    @Query("SELECT b FROM Booking b WHERE b.userId = :userId AND b.status = 'ACTIVE' AND " +
           "CONCAT(b.date, ' ', b.showtime) > :currentDateTime")
    List<Booking> findValidBookingsByUserId(String userId, LocalDateTime currentDateTime);

    @Query("SELECT b FROM Booking b WHERE b.status = 'ACTIVE' AND " +
           "CONCAT(b.date, ' ', b.showtime) < :currentDateTime")
    List<Booking> findActiveBookingsBefore(@Param("currentDateTime") LocalDateTime currentDateTime);
}