package com.example.cinema.controller;

import com.example.cinema.dto.BookingDTO;
import com.example.cinema.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
    public ResponseEntity<Map<String, Object>> createBooking(@RequestBody BookingDTO bookingDTO) {
        try {
            BookingDTO savedBooking = bookingService.createBooking(bookingDTO);
            Map<String, Object> response = new HashMap<>();
            response.put("data", savedBooking);
            response.put("message", "Booking request submitted successfully");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error creating booking: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

@GetMapping
public ResponseEntity<List<BookingDTO>> getUserBookings(
        Authentication authentication,
        @RequestParam(defaultValue = "all") String filter,
        @RequestParam(required = false) String userId) {
    if (authentication == null || !authentication.isAuthenticated()) {
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
    List<BookingDTO> bookings = bookingService.getBookingsByUserId(userId, filter);
    return ResponseEntity.ok(bookings);
}

    @GetMapping("/latest")
    public ResponseEntity<BookingDTO> getLatestBooking(@RequestParam(required = false) String userId) {
        BookingDTO booking = bookingService.getLatestBookingByUserId(userId);
        return booking != null ? ResponseEntity.ok(booking) : ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getBookingCount(@RequestParam("userId") String userId) {
        long count = bookingService.getBookingsByUserId(userId, null).size();
        return ResponseEntity.ok(count);
    }

    @PostMapping("/cancel/{id}")
    public ResponseEntity<Map<String, Object>> requestCancelBooking(@PathVariable Long id) {
        try {
            bookingService.requestCancelBooking(id);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Cancellation request submitted successfully");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        } catch (IllegalStateException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error requesting cancellation: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/check-seats")
    public ResponseEntity<List<String>> getBookedSeats(
            @RequestParam String movieId,
            @RequestParam String date,
            @RequestParam String showtime,
            @RequestParam Integer roomId) {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        LocalDate bookingDate = LocalDate.parse(date);
        LocalDateTime showDateTime = LocalDateTime.of(bookingDate,
                LocalTime.parse(showtime, DateTimeFormatter.ofPattern("HH:mm")));
        if (showDateTime.isBefore(now)) {
            return ResponseEntity.ok(List.of());
        }
        List<String> bookedSeats = bookingService.getBookedSeats(movieId, LocalDate.parse(date), showtime, roomId);
        return ResponseEntity.ok(bookedSeats);
    }
}