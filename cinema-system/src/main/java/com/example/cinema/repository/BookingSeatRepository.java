package com.example.cinema.repository;

import com.example.cinema.entity.BookingSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.util.List;

public interface BookingSeatRepository extends JpaRepository<BookingSeat, Long> {
    @Query("SELECT bs.seat FROM BookingSeat bs WHERE bs.movieId = :movieId AND bs.date = :date AND bs.showtime = :showtime")
    List<String> findSeatsByMovieIdAndDateAndShowtime(String movieId, LocalDate date, String showtime);

    List<BookingSeat> findByBookingId(Long bookingId);
}