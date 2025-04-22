package com.example.cinema.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.cinema.entity.Booking;

public interface BookingRepository extends JpaRepository<Booking, Long> { 
    List<Booking> findByUserId(String userId);
    @Query("SELECT DISTINCT b.movieId FROM Booking b WHERE b.userId = :userId")
    List<String> findDistinctMovieIdsByUserId(String userId);
}