package com.example.cinema.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String movieId;

    @Column(nullable = false)
    private String movieTitle;

    @Column(nullable = false)
    private String showtime;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private double total;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "room_id", nullable = false)
    private Long roomId;

    @Column(nullable = false)
    private String status = "WAITING_BOOKING";

    // Getters
    public Long getId() { return id; }
    public String getMovieId() { return movieId; }
    public String getMovieTitle() { return movieTitle; }
    public String getShowtime() { return showtime; }
    public LocalDate getDate() { return date; }
    public double getTotal() { return total; }
    public String getUserId() { return userId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Long getRoomId() { return roomId; }
    public String getStatus() { return status; }

    // Setters
    public void setMovieId(String movieId) { this.movieId = movieId; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }
    public void setShowtime(String showtime) { this.showtime = showtime; }
    public void setDate(LocalDate date) { this.date = date; }
    public void setTotal(double total) { this.total = total; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }
    public void setStatus(String status) { this.status = status; }
}