package com.example.demo.repository;

import com.example.demo.entity.Bookings;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookingsRepository extends JpaRepository<Bookings, Long> {
    @Query("SELECT b FROM Bookings b WHERE b.roomId = :roomId AND b.date = :date AND b.showtime = :showtime AND b.userId = :userId")
    Optional<Bookings> findByRoomIdAndDateAndShowtimeAndUserId(Long roomId, String date, String showtime, String userId);
}