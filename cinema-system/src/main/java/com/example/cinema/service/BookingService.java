package com.example.cinema.service;

import com.example.cinema.dto.BookingDTO;
import com.example.cinema.entity.Booking;
import com.example.cinema.entity.BookingSeat; // Thêm import
import com.example.cinema.repository.BookingRepository;
import com.example.cinema.repository.BookingSeatRepository;
import com.example.cinema.service.BookingSeatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookingService {
    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);
    private final BookingRepository bookingRepository;
    private final BookingSeatRepository bookingSeatRepository;
    private final BookingSeatService bookingSeatService;

    public BookingService(BookingRepository bookingRepository, BookingSeatRepository bookingSeatRepository, BookingSeatService bookingSeatService) {
        this.bookingRepository = bookingRepository;
        this.bookingSeatRepository = bookingSeatRepository;
        this.bookingSeatService = bookingSeatService;
    }

    @Scheduled(cron = "0 0 * * * *") // Run hourly
    @Transactional
    public void expireOldBookings() {
        try {
            LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
            LocalDate currentDate = now.toLocalDate();
            String currentTime = now.format(DateTimeFormatter.ofPattern("HH:mm"));
            
            List<Booking> expiredBookings = bookingRepository.findActiveBookingsBefore(currentDate, currentTime);
            if (expiredBookings.isEmpty()) {
                logger.info("No bookings to expire at {}", now);
                return;
            }

            for (Booking booking : expiredBookings) {
                booking.setStatus("EXPIRED");
                bookingRepository.save(booking);
                bookingSeatRepository.deleteByBookingId(booking.getId());
                logger.info("Expired booking ID {} for movie '{}' on {} at {}", 
                           booking.getId(), booking.getMovieTitle(), booking.getDate(), booking.getShowtime());
            }
            logger.info("Expired {} bookings at {}", expiredBookings.size(), now);
        } catch (Exception e) {
            logger.error("Error expiring bookings: {}", e.getMessage(), e);
        }
    }

    public List<BookingDTO> getBookingsByUserId(String userId, String filter) {
        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        LocalDate currentDate = now.toLocalDate();
        String currentTime = now.format(DateTimeFormatter.ofPattern("HH:mm"));

        if ("valid".equalsIgnoreCase(filter)) {
            bookings = bookingRepository.findValidBookingsByUserId(userId, currentDate, currentTime);
        } else if ("active".equalsIgnoreCase(filter)) {
            bookings = bookingRepository.findByUserIdAndStatus(userId, "ACTIVE");
        } else if ("waiting_booking".equalsIgnoreCase(filter)) {
            bookings = bookingRepository.findByUserIdAndStatus(userId, "WAITING_BOOKING");
        } else if ("waiting_cancel".equalsIgnoreCase(filter)) {
            bookings = bookingRepository.findByUserIdAndStatus(userId, "WAITING_CANCEL");
        } else if ("cancelled".equalsIgnoreCase(filter)) {
            bookings = bookingRepository.findByUserIdAndStatus(userId, "CANCELLED");
        } else if ("expired".equalsIgnoreCase(filter)) {
            bookings = bookingRepository.findByUserIdAndStatus(userId, "EXPIRED");
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
            dto.setStatus(booking.getStatus());
            List<String> seats = bookingSeatRepository.findByBookingId(booking.getId())
                    .stream().map(BookingSeat::getSeat).collect(Collectors.toList());
            dto.setSeats(seats);
            return dto;
        }).collect(Collectors.toList());
    }

    public BookingDTO getLatestBookingByUserId(String userId) {
        PageRequest pageable = PageRequest.of(0, 1);
        List<Booking> bookings = bookingRepository.findLatestByUserId(userId, pageable);
        if (bookings.isEmpty()) {
            return null;
        }
        Booking booking = bookings.get(0);
        BookingDTO dto = new BookingDTO();
        dto.setId(booking.getId());
        dto.setMovieId(booking.getMovieId());
        dto.setMovieTitle(booking.getMovieTitle());
        dto.setShowtime(booking.getShowtime());
        dto.setDate(booking.getDate().toString());
        dto.setTotal(booking.getTotal());
        dto.setUserId(booking.getUserId());
        dto.setRoomId(booking.getRoomId());
        dto.setStatus(booking.getStatus());
        List<String> seats = bookingSeatRepository.findByBookingId(booking.getId())
                .stream().map(BookingSeat::getSeat).collect(Collectors.toList());
        dto.setSeats(seats);
        return dto;
    }

    @Transactional
    public BookingDTO createBooking(BookingDTO bookingDTO) {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        LocalDate bookingDate = LocalDate.parse(bookingDTO.getDate());
        LocalDateTime showDateTime = LocalDateTime.of(bookingDate,
                LocalTime.parse(bookingDTO.getShowtime(), DateTimeFormatter.ofPattern("HH:mm")));
        if (showDateTime.isBefore(now)) {
            throw new IllegalArgumentException("Cannot book past showtimes");
        }

        Booking booking = new Booking();
        booking.setMovieId(bookingDTO.getMovieId());
        booking.setMovieTitle(bookingDTO.getMovieTitle());
        booking.setShowtime(bookingDTO.getShowtime());
        booking.setDate(LocalDate.parse(bookingDTO.getDate()));
        booking.setTotal(bookingDTO.getTotal());
        booking.setUserId(bookingDTO.getUserId());
        booking.setRoomId(bookingDTO.getRoomId());
        booking.setStatus("WAITING_BOOKING");
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
        bookingDTO.setStatus(savedBooking.getStatus());
        return bookingDTO;
    }

    @Transactional
    public void requestCancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        LocalDateTime showDateTime = LocalDateTime.of(booking.getDate(), 
                LocalTime.parse(booking.getShowtime()));
        if (showDateTime.isBefore(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")))) {
            throw new IllegalStateException("Cannot cancel past bookings");
        }
        if (!"ACTIVE".equalsIgnoreCase(booking.getStatus())) {
            throw new IllegalStateException("Booking is not active");
        }
        booking.setStatus("WAITING_CANCEL");
        bookingRepository.save(booking);
    }

    public List<String> getBookedSeats(String movieId, LocalDate date, String showtime, Long roomId) {
        return bookingSeatService.getBookedSeats(movieId, date, showtime, roomId);
    }
}