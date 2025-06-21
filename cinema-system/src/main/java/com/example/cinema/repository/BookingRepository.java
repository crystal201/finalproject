package com.example.cinema.repository;

import com.example.cinema.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(String userId);

    @Query("SELECT b FROM Booking b WHERE b.userId = :userId AND b.date >= :currentDate AND b.showtime > :currentTime AND b.status IN ('WAITING_BOOKING', 'ACTIVE')")
    List<Booking> findValidBookingsByUserId(@Param("userId") String userId, @Param("currentDate") LocalDate currentDate, @Param("currentTime") String currentTime);

    @Query("SELECT b FROM Booking b WHERE b.userId = :userId AND b.status = :status")
    List<Booking> findByUserIdAndStatus(@Param("userId") String userId, @Param("status") String status);

    @Query("SELECT b FROM Booking b WHERE b.userId = :userId ORDER BY b.createdAt DESC")
    List<Booking> findLatestByUserId(@Param("userId") String userId, org.springframework.data.domain.Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.date < :currentDate OR (b.date = :currentDate AND b.showtime <= :currentTime) AND b.status = 'ACTIVE'")
    List<Booking> findActiveBookingsBefore(@Param("currentDate") LocalDate currentDate, @Param("currentTime") String currentTime);
}