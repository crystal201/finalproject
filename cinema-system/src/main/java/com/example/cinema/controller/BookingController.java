package com.example.cinema.controller;

import com.example.cinema.dto.BookingDTO;
import com.example.cinema.dto.MovieDTO;
import com.example.cinema.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createBooking(@RequestBody BookingDTO bookingDTO, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Chưa đăng nhập");
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }
        try {
            String userId = authentication.getName();
            bookingDTO.setUserId(userId);
            BookingDTO savedBooking = bookingService.createBooking(bookingDTO);
            Map<String, Object> response = new HashMap<>();
            response.put("data", savedBooking);
            response.put("message", "Đặt vé thành công");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Lỗi khi đặt vé: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<List<BookingDTO>> getUserBookings(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String userId = authentication.getName();
        List<BookingDTO> bookings = bookingService.getBookingsByUserId(userId);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/movies")
    public ResponseEntity<List<MovieDTO>> getBookedMovies(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String userId = authentication.getName();
        List<MovieDTO> movies = bookingService.getBookedMovies(userId);
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/check-seats")
    public ResponseEntity<List<String>> getBookedSeats(
        @RequestParam String movieId,
        @RequestParam String date,
        @RequestParam String showtime
    ) {
        List<String> bookedSeats = bookingService.getBookedSeats(movieId, LocalDate.parse(date), showtime);
        return ResponseEntity.ok(bookedSeats);
    }
}