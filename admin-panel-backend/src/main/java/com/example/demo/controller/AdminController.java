package com.example.demo.controller;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://157.66.219.181:8082")
@RequestMapping("/api")
public class AdminController {

    @Autowired
    private RoomRepository roomRepo;

    @Autowired
    private BookingCancellationRepository cancelRepo;

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

    @PutMapping("/cancellations/{id}/approve")
    public BookingCancellation approveCancellation(@PathVariable Long id) {
        BookingCancellation cancel = cancelRepo.findById(id).orElseThrow();
        cancel.setStatus("APPROVED");
        return cancelRepo.save(cancel);
    }

    @GetMapping("api/users")
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
                return new BookingResponse(
                    booking.getId(),
                    booking.getMovieTitle(),
                    booking.getShowtime(),
                    booking.getDate(),
                    booking.getTotal(),
                    booking.getStatus(),
                    seats.stream().map(BookingSeats::getSeat).collect(Collectors.toList()),
                    room != null ? room.getRoomName() : null
                );
            })
            .collect(Collectors.toList());
    }

    @GetMapping("/bookings/occupied-seats")
    public List<OccupiedSeatResponse> getOccupiedSeats(
            @RequestParam(required = false) Long roomId,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String showtime) {
        return bookingsRepo.findAll().stream()
            .filter(b -> "ACTIVE".equalsIgnoreCase(b.getStatus()))
            .filter(b -> roomId == null || b.getRoomId().equals(roomId)) // Sửa lỗi
            .filter(b -> date == null || b.getDate().equals(date))      // Sửa lỗi
            .filter(b -> showtime == null || b.getShowtime().equals(showtime)) // Sửa lỗi
            .flatMap(booking -> bookingSeatsRepo.findByBookingId(booking.getId()).stream()
                .map(seat -> new OccupiedSeatResponse(
                    booking.getRoomId(),
                    seat.getSeat(),
                    booking.getDate(),
                    booking.getShowtime()
                )))
            .collect(Collectors.toList());
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

    public BookingResponse(Long id, String movieTitle, String showtime, String date, Double total, String status, List<String> seats, String roomName) {
        this.id = id;
        this.movieTitle = movieTitle;
        this.showtime = showtime;
        this.date = date;
        this.total = total;
        this.status = status;
        this.seats = seats;
        this.roomName = roomName;
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
}

class OccupiedSeatResponse {
    private Long roomId;
    private String seat;
    private String date;
    private String showtime;

    public OccupiedSeatResponse(Long roomId, String seat, String date, String showtime) {
        this.roomId = roomId;
        this.seat = seat;
        this.date = date;
        this.showtime = showtime;
    }

    // Getters
    public Long getRoomId() { return roomId; }
    public String getSeat() { return seat; }
    public String getDate() { return date; }
    public String getShowtime() { return showtime; }
}