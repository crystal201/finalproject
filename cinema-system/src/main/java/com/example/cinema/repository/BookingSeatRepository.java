package com.example.cinema.repository;

import com.example.cinema.entity.BookingSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface BookingSeatRepository extends JpaRepository<BookingSeat, Long> {
    @Query("SELECT bs.seat FROM BookingSeat bs JOIN Booking b ON bs.bookingId = b.id " +
           "WHERE bs.movieId = :movieId AND bs.date = :date AND bs.showtime = :showtime " +
           "AND b.status = 'ACTIVE'")
    List<String> findSeatsByMovieIdAndDateAndShowtime(
            @Param("movieId") String movieId,
            @Param("date") LocalDate date,
            @Param("showtime") String showtime);

    @Query("SELECT bs.seat FROM BookingSeat bs JOIN Booking b ON bs.bookingId = b.id " +
           "WHERE bs.movieId = :movieId AND bs.date = :date AND bs.showtime = :showtime " +
           "AND bs.roomId = :roomId AND b.status = 'ACTIVE'")
    List<String> findSeatsByMovieIdAndDateAndShowtimeAndRoomId(
            @Param("movieId") String movieId,
            @Param("date") LocalDate date,
            @Param("showtime") String showtime,
            @Param("roomId") Integer roomId);

    List<BookingSeat> findByBookingId(Long bookingId);
    void deleteByBookingId(Long bookingId);
}