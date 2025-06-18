package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class BookingSeats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long bookingId;
    private String date;
    private String movieId;
    private long roomId;
    private String seat;
    private String showtime;

    public Long getId() { return id; }
    public String getSeat() { return seat; }
    public String getDate() { return date; }
    public String getShowtime() { return showtime; }
    public Long getRoomId() { return roomId; }
}