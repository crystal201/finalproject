package com.example.cinema.service;

import com.example.cinema.dto.BookingDTO;
import com.example.cinema.entity.Booking;
import com.example.cinema.entity.BookingSeat;
import com.example.cinema.repository.BookingRepository;
import com.example.cinema.repository.BookingSeatRepository;

import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.System.Logger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(BookingService.class);
    private final BookingRepository bookingRepository;
    private final BookingSeatRepository bookingSeatRepository;
    private final BookingSeatService bookingSeatService;

    public BookingService(BookingRepository bookingRepository, BookingSeatRepository bookingSeatRepository, BookingSeatService bookingSeatService) {
        this.bookingRepository = bookingRepository;
        this.bookingSeatRepository = bookingSeatRepository;
        this.bookingSeatService = bookingSeatService;
    }
    @Transactional
@Scheduled(cron = "0 0 * * * *") // Run hourly
public void expireOldBookings() {
    try {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        List<Booking> expiredBookings = bookingRepository.findActiveBookingsBefore(now);
        if (expiredBookings.isEmpty()) {
            logger.info("No bookings to expire at {}", now);
            return;
        }

        for (Booking booking : expiredBookings) {
            booking.setStatus(Booking.Status.EXPIRED);
            bookingRepository.save(booking);
            bookingSeatRepository.deleteByBookingId(booking.getId());
            logger.info("Expired booking ID {} for showtime {} on {}", 
                        booking.getId(), booking.getShowtime(), booking.getDate());
        }
        logger.info("Expired {} bookings at {}", expiredBookings.size(), now);
    } catch (Exception e) {
        logger.error("Error expiring bookings: {}", e.getMessage(), e);
    }
}

    @Transactional
    public BookingDTO createBooking(BookingDTO bookingDTO) {
        Booking booking = new Booking();
        booking.setMovieId(bookingDTO.getMovieId());
        booking.setMovieTitle(bookingDTO.getMovieTitle());
        booking.setShowtime(bookingDTO.getShowtime());
        booking.setDate(LocalDate.parse(bookingDTO.getDate()));
        booking.setTotal(bookingDTO.getTotal());
        booking.setUserId(bookingDTO.getUserId());
        booking.setRoomId(bookingDTO.getRoomId());
        booking.setStatus(Booking.Status.ACTIVE);
        Booking savedBooking = bookingRepository.save(booking);

        bookingSeatService.saveSeats(
            savedBooking.getId(),
            bookingDTO.getSeats(),
            bookingDTO.getMovieId(),
            LocalDate.parse(bookingDTO.getDate()),
            bookingDTO.getShowtime(),
            bookingDTO.getRoomId()
        );

        bookingDTO.setId(savedBooking.getId());
        bookingDTO.setStatus(savedBooking.getStatus().name());
        return bookingDTO;
    }

    public List<BookingDTO> getBookingsByUserId(String userId, String filter) {
        List<Booking> bookings;
        if ("valid".equalsIgnoreCase(filter)) {
            bookings = bookingRepository.findValidBookingsByUserId(userId, LocalDateTime.now());
        } else {
            bookings = bookingRepository.findByUserId(userId);
        }
        return bookings.stream().map(booking -> {
            BookingDTO dto = new BookingDTO();
            dto.setId(booking.getId());
            dto.setMovieId(booking.getMovieId());
            dto.setMovieTitle(booking.getMovieTitle());
            dto.setShowtime(booking.getShowtime());
            dto.setDate(booking.getDate().toString());
            dto.setTotal(booking.getTotal());
            dto.setUserId(booking.getUserId());
            dto.setRoomId(booking.getRoomId());
            dto.setStatus(booking.getStatus().name());
            List<String> seats = bookingSeatRepository.findByBookingId(booking.getId())
                    .stream().map(BookingSeat::getSeat).collect(Collectors.toList());
            dto.setSeats(seats);
            return dto;
        }).collect(Collectors.toList());
    }

@Transactional
public void cancelBooking(Long bookingId, String userId) {
    Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
    if (!booking.getUserId().equals(userId)) {
        throw new SecurityException("Unauthorized to cancel this booking");
    }
    LocalDateTime showDateTime = LocalDateTime.parse(
        booking.getDate() + " " + booking.getShowtime(),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    );
    if (showDateTime.isBefore(LocalDateTime.now())) {
        throw new IllegalStateException("Cannot cancel past bookings");
    }
    if (booking.getStatus() == Booking.Status.CANCELLED) {
        throw new IllegalStateException("Booking already cancelled");
    }
    if (booking.getStatus() != Booking.Status.ACTIVE) {
        throw new IllegalStateException("Booking is not active (already cancelled or expired)");
    }
    bookingSeatRepository.deleteByBookingId(bookingId);
    booking.setStatus(Booking.Status.CANCELLED);
    bookingRepository.save(booking);
}

    public List<String> getBookedSeats(String movieId, LocalDate date, String showtime, Integer roomId) {
        return bookingSeatService.getBookedSeats(movieId, date, showtime, roomId);
    }
}