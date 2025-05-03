package com.example.cinema.dto;

import java.time.LocalDateTime;
import java.util.List;

public class BookingDTO {
    private Long id;
    private String movieId;
    private String movieTitle;
    private String showtime;
    private String date;
    private List<String> seats;
    private Double total;
    private String userId;
    private LocalDateTime createdAt;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getMovieId() { return movieId; }
    public void setMovieId(String movieId) { this.movieId = movieId; }
    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }
    public String getShowtime() { return showtime; }
    public void setShowtime(String showtime) { this.showtime = showtime; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public List<String> getSeats() { return seats; }
    public void setSeats(List<String> seats) { this.seats = seats; }
    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}