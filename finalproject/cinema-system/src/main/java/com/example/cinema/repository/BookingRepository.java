package com.example.cinema.repository;

import com.example.cinema.entity.Booking;

import org.springdoc.core.converters.models.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserIdAndStatus(String userId, Booking.Status status);
    List<Booking> findByUserId(String userId);

    @Query("SELECT b FROM Booking b WHERE b.userId = :userId AND b.status = 'ACTIVE' AND " +
           "b.date < :currentDate OR (b.date = :currentDate AND b.showtime < :currentTime)")
    List<Booking> findValidBookingsByUserId(@Param("userId") String userId, 
                                          @Param("currentDate") LocalDate currentDate,
                                          @Param("currentTime") String currentTime);

    @Query("SELECT b FROM Booking b WHERE b.status = 'ACTIVE' AND " +
           "(b.date < :currentDate OR (b.date = :currentDate AND b.showtime < :currentTime))")
    List<Booking> findActiveBookingsBefore(@Param("currentDate") LocalDate currentDate,
                                         @Param("currentTime") String currentTime);
    @Query("SELECT b FROM Booking b WHERE b.userId = :userId ORDER BY b.createdAt DESC")
    List<Booking> findLatestByUserId(@Param("userId") String userId, PageRequest pageable);
}