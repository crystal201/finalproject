package com.example.demo.controller;

import com.example.demo.entity.*;
import com.example.demo.repository.*;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private RoomRepository roomRepo;

    @Autowired
    private UsersRepository usersRepo;

    @Autowired
    private BookingsRepository bookingsRepo;

    @Autowired
    private BookingSeatsRepository bookingSeatsRepo;

    @GetMapping("/rooms")
    public List<Room> getRooms() {
        return roomRepo.findAll();
    }

    @PostMapping("/rooms")
    public Room addRoom(@RequestBody Room room) {
        return roomRepo.save(room);
    }

    @DeleteMapping("/rooms/{id}")
    public void deleteRoom(@PathVariable Long id) {
        roomRepo.deleteById(id);
    }

    @GetMapping("/users")
    public List<UserResponse> getUsers() {
        return usersRepo.findAll().stream()
            .map(user -> new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPhone(),
                user.getRole()
            ))
            .collect(Collectors.toList());
    }

    @GetMapping("/bookings")
    public List<BookingResponse> getBookings() {
        return bookingsRepo.findAll().stream()
            .map(booking -> {
                List<BookingSeats> seats = bookingSeatsRepo.findByBookingId(booking.getId());
                Room room = roomRepo.findById(booking.getRoomId()).orElse(null);
                String userId = booking.getUserId();
                Users user = null;
                try {
                    long userIdLong = Long.parseLong(userId);
                    logger.info("Attempting to find user with id: {} for booking {}", userIdLong, booking.getId());
                    user = usersRepo.findById(userIdLong)
                        .orElseGet(() -> {
                            logger.warn("User with id {} not found for booking {}, creating default", userIdLong, booking.getId());
                            return new Users();
                        });
                } catch (NumberFormatException e) {
                    logger.error("Failed to parse userId {} for booking {}: {}", userId, booking.getId(), e.getMessage(), e);
                    user = new Users();
                }
                String username = user.getUsername() != null ? user.getUsername() : "Unknown";
                logger.info("Mapped booking {} to userId: {}, username: {}", booking.getId(), userId, username);
                return new BookingResponse(
                    booking.getId(),
                    booking.getMovieTitle(),
                    booking.getShowtime(),
                    booking.getDate(),
                    booking.getTotal(),
                    booking.getStatus(),
                    seats.stream().map(BookingSeats::getSeat).collect(Collectors.toList()),
                    room != null ? room.getRoomName() : null,
                    userId,
                    username
                );
            })
            .collect(Collectors.toList());
    }

        @GetMapping("/bookings/occupied-seats")
public List<GroupedOccupiedSeatResponse> getOccupiedSeats(
        @RequestParam(required = false) Long roomId,
        @RequestParam(required = false) String date,
        @RequestParam(required = false) String showtime) {
    return bookingsRepo.findAll().stream()
            .filter(b -> "ACTIVE".equalsIgnoreCase(b.getStatus()))
            .filter(b -> roomId == null || b.getRoomId().equals(roomId))
            .filter(b -> date == null || b.getDate().equals(date))
            .filter(b -> showtime == null || b.getShowtime().equals(showtime))
            .collect(Collectors.groupingBy(
                    booking -> new GroupedOccupiedSeatKey(
                            booking.getRoomId(),
                            booking.getDate(),
                            booking.getShowtime(),
                            booking.getUserId()
                    )
            ))
            .entrySet().stream()
            .map(entry -> {
                GroupedOccupiedSeatKey key = entry.getKey();
                // Lấy tất cả bookings khớp với key
                List<Bookings> bookings = bookingsRepo.findAll().stream()
                        .filter(b -> b.getRoomId().equals(key.getRoomId())
                                && b.getDate().equals(key.getDate())
                                && b.getShowtime().equals(key.getShowtime())
                                && b.getUserId().equals(key.getUserId())
                                && "ACTIVE".equalsIgnoreCase(b.getStatus()))
                        .collect(Collectors.toList());
                List<BookingSeats> seats = bookings.stream()
                        .flatMap(b -> bookingSeatsRepo.findByBookingId(b.getId()).stream())
                        .collect(Collectors.toList());
                Users user = null;
                try {
                    long userIdLong = Long.parseLong(key.getUserId());
                    user = usersRepo.findById(userIdLong)
                            .orElseGet(() -> {
                                logger.warn("User with id {} not found, creating default", userIdLong);
                                return new Users();
                            });
                } catch (NumberFormatException e) {
                    logger.error("Failed to parse userId {}: {}", key.getUserId(), e.getMessage());
                    user = new Users();
                }
                // Lấy movieTitle từ booking đầu tiên nếu có
                String movieTitle = bookings.isEmpty() ? "N/A" : (bookings.get(0).getMovieTitle() != null ? bookings.get(0).getMovieTitle() : "N/A");
                logger.info("Key: roomId={}, date={}, showtime={}, userId={}, bookings found: {}, movieTitle: {}",
                        key.getRoomId(), key.getDate(), key.getShowtime(), key.getUserId(), bookings.size(), movieTitle);

                return new GroupedOccupiedSeatResponse(
                        key.getRoomId(),
                        seats.stream().map(BookingSeats::getSeat).collect(Collectors.toList()),
                        key.getDate(),
                        key.getShowtime(),
                        key.getUserId(),
                        user.getUsername() != null ? user.getUsername() : "Unknown",
                        movieTitle
                );
            })
            .collect(Collectors.toList());
    }
    @PostMapping("/bookings/accept/{id}")
    public ResponseEntity<Map<String, Object>> acceptBooking(@PathVariable Long id) {
        Bookings booking = bookingsRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        if (!"WAITING_BOOKING".equalsIgnoreCase(booking.getStatus())) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Booking is not in WAITING_BOOKING state");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
        booking.setStatus("ACTIVE");
        bookingsRepo.save(booking);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Booking accepted successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/bookings/reject/{id}")
    @Transactional
    public ResponseEntity<Map<String, Object>> rejectBooking(@PathVariable Long id) {
        Bookings booking = bookingsRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        if (!"WAITING_BOOKING".equalsIgnoreCase(booking.getStatus())) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Booking is not in WAITING_BOOKING state");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
        try {
            bookingSeatsRepo.deleteByBookingId(id); // Xóa ghế trước
            booking.setStatus("REJECTED");
            bookingsRepo.save(booking);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Booking rejected successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error rejecting booking: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/bookings/accept-cancel/{id}")
    @Transactional
    public ResponseEntity<Map<String, Object>> acceptCancelBooking(@PathVariable Long id) {
        Bookings booking = bookingsRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        if (!"WAITING_CANCEL".equalsIgnoreCase(booking.getStatus())) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Booking is not in WAITING_CANCEL state");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
        try {
            bookingSeatsRepo.deleteByBookingId(id); // Xóa ghế trước
            booking.setStatus("CANCELLED");
            bookingsRepo.save(booking);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Cancellation accepted successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error accepting cancellation: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/bookings/reject-cancel/{id}")
    public ResponseEntity<Map<String, Object>> rejectCancelBooking(@PathVariable Long id) {
        Bookings booking = bookingsRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        if (!"WAITING_CANCEL".equalsIgnoreCase(booking.getStatus())) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Booking is not in WAITING_CANCEL state");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
        booking.setStatus("ACTIVE");
        bookingsRepo.save(booking);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Cancellation rejected successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String role;

    public UserResponse(Long id, String username, String email, String phone, String role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.role = role;
    }

    // Getters
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getRole() { return role; }
}

class BookingResponse {
    private Long id;
    private String movieTitle;
    private String showtime;
    private String date;
    private Double total;
    private String status;
    private List<String> seats;
    private String roomName;
    @JsonProperty("userId")
    private String userId;
    @JsonProperty("username")
    private String username;

    public BookingResponse(Long id, String movieTitle, String showtime, String date, Double total, String status, List<String> seats, String roomName, String userId, String username) {
        this.id = id;
        this.movieTitle = movieTitle;
        this.showtime = showtime;
        this.date = date;
        this.total = total;
        this.status = status;
        this.seats = seats;
        this.roomName = roomName;
        this.userId = userId;
        this.username = username;
    }

    // Getters
    public Long getId() { return id; }
    public String getMovieTitle() { return movieTitle; }
    public String getShowtime() { return showtime; }
    public String getDate() { return date; }
    public Double getTotal() { return total; }
    public String getStatus() { return status; }
    public List<String> getSeats() { return seats; }
    public String getRoomName() { return roomName; }
    public String getUserId() { return userId; }
    public String getUsername() { return username; }
}

class OccupiedSeatResponse {
    private Long roomId;
    private String seat;
    private String date;
    private String showtime;
    @JsonProperty("userId")
    private String userId;
    @JsonProperty("username")
    private String username;

    public OccupiedSeatResponse(Long roomId, String seat, String date, String showtime, String userId, String username) {
        this.roomId = roomId;
        this.seat = seat;
        this.date = date;
        this.showtime = showtime;
        this.userId = userId;
        this.username = username;
    }
    // Getters
    public Long getRoomId() { return roomId; }
    public String getSeat() { return seat; }
    public String getDate() { return date; }
    public String getShowtime() { return showtime; }
    public String getUserId() { return userId; }
    public String getUsername() { return username; }
}
 class GroupedOccupiedSeatKey {
    private final Long roomId;
    private final String date;
    private final String showtime;
    private final String userId;

    public GroupedOccupiedSeatKey(Long roomId, String date, String showtime, String userId) {
        this.roomId = roomId;
        this.date = date;
        this.showtime = showtime;
        this.userId = userId;
    };
    public Long getRoomId(){ return roomId; }
    public String getDate(){ return date; }
    public String getShowtime(){ return showtime; }
    public String getUserId(){ return userId; }




    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupedOccupiedSeatKey that = (GroupedOccupiedSeatKey) o;
        return roomId.equals(that.roomId) &&
                date.equals(that.date) &&
                showtime.equals(that.showtime) &&
                userId.equals(that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomId, date, showtime, userId);
    }
}
class GroupedOccupiedSeatResponse {
    private final Long roomId;
    private final List<String> seats;
    private final String date;
    private final String showtime;
    @JsonProperty("userId")
    private final String userId;
    @JsonProperty("username")
    private final String username;
    private final String movieTitle;

    public GroupedOccupiedSeatResponse(Long roomId, List<String> seats, String date, String showtime, String userId, String username, String movieTitle) {
        this.roomId = roomId;
        this.seats = seats;
        this.date = date;
        this.showtime = showtime;
        this.userId = userId;
        this.username = username;
        this.movieTitle = movieTitle;
    }

    // Getters
    public Long getRoomId() { return roomId; }
    public List<String> getSeats() { return seats; }
    public String getDate() { return date; }
    public String getShowtime() { return showtime; }
    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getMovieTitle() { return movieTitle; }
}