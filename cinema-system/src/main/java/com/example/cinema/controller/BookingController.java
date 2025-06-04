package com.example.cinema.controller;

import com.example.cinema.dto.BookingDTO;
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
    public ResponseEntity<List<BookingDTO>> getUserBookings(
            Authentication authentication,
            @RequestParam(defaultValue = "all") String filter) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String userId = authentication.getName();
        List<BookingDTO> bookings = bookingService.getBookingsByUserId(userId, filter);
        return ResponseEntity.ok(bookings);
    }
    @GetMapping("/latest")
    public ResponseEntity<BookingDTO> getLatestBooking(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String userId = authentication.getName();
        BookingDTO booking = bookingService.getLatestBookingByUserId(userId);
        return booking != null ? ResponseEntity.ok(booking) : ResponseEntity.noContent().build();
    }
    @GetMapping("/count")
    public ResponseEntity<Long> getBookingCount(@RequestParam("user_id") String userId) {
        long count = bookingService.getBookingsByUserId(userId, null).size();
        return ResponseEntity.ok(count);
    }
    @PostMapping("/cancel/{id}")
    public ResponseEntity<Map<String, Object>> cancelBooking(
            @PathVariable Long id,
            Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Chưa đăng nhập");
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }
        try {
            String userId = authentication.getName();
            bookingService.cancelBooking(id, userId);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Hủy vé thành công");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | SecurityException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        } catch (IllegalStateException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Lỗi khi hủy vé: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/check-seats")
    public ResponseEntity<List<String>> getBookedSeats(
            @RequestParam String movieId,
            @RequestParam String date,
            @RequestParam String showtime,
            @RequestParam Integer roomId) {
        List<String> bookedSeats = bookingService.getBookedSeats(movieId, LocalDate.parse(date), showtime, roomId);
        return ResponseEntity.ok(bookedSeats);
    }
}