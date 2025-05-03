package com.example.cinema.service;

import com.example.cinema.dto.BookingDTO;
import com.example.cinema.dto.MovieDTO;
import com.example.cinema.entity.Booking;
import com.example.cinema.entity.BookingSeat;
import com.example.cinema.repository.BookingRepository;
import com.example.cinema.repository.BookingSeatRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {
    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);
    private final BookingRepository bookingRepository;
    private final BookingSeatRepository bookingSeatRepository;
    private final MovieService movieService;

    public BookingService(BookingRepository bookingRepository, BookingSeatRepository bookingSeatRepository, MovieService movieService) {
        this.bookingRepository = bookingRepository;
        this.bookingSeatRepository = bookingSeatRepository;
        this.movieService = movieService;
    }

    public BookingDTO createBooking(BookingDTO bookingDTO) {
        logger.info("Starting createBooking: {}", bookingDTO);
        try {
            // Kiểm tra dữ liệu đầu vào
            if (bookingDTO.getMovieId() == null || bookingDTO.getMovieId().isEmpty()) {
                throw new IllegalArgumentException("movieId không được để trống");
            }
            if (bookingDTO.getMovieTitle() == null || bookingDTO.getMovieTitle().isEmpty()) {
                throw new IllegalArgumentException("movieTitle không được để trống");
            }
            if (bookingDTO.getShowtime() == null || bookingDTO.getShowtime().isEmpty()) {
                throw new IllegalArgumentException("showtime không được để trống");
            }
            if (bookingDTO.getDate() == null || bookingDTO.getDate().isEmpty()) {
                throw new IllegalArgumentException("date không được để trống");
            }
            if (bookingDTO.getUserId() == null || bookingDTO.getUserId().isEmpty()) {
                throw new IllegalArgumentException("userId không được để trống");
            }
            if (bookingDTO.getSeats() == null || bookingDTO.getSeats().isEmpty()) {
                throw new IllegalArgumentException("Phải chọn ít nhất một ghế");
            }
            if (bookingDTO.getTotal() <= 0) {
                throw new IllegalArgumentException("total phải lớn hơn 0");
            }

            // Parse date
            LocalDate bookingDate;
            try {
                bookingDate = LocalDate.parse(bookingDTO.getDate());
            } catch (DateTimeParseException e) {
                logger.error("Lỗi parse date: {}", bookingDTO.getDate(), e);
                throw new IllegalArgumentException("Định dạng ngày không hợp lệ, phải là yyyy-MM-dd: " + bookingDTO.getDate());
            }

            // Kiểm tra ghế đã đặt
            List<String> bookedSeats = getBookedSeats(bookingDTO.getMovieId(), bookingDate, bookingDTO.getShowtime());
            List<String> requestedSeats = bookingDTO.getSeats();
            if (requestedSeats.stream().anyMatch(bookedSeats::contains)) {
                throw new IllegalStateException("Một số ghế đã được đặt: " + requestedSeats.stream().filter(bookedSeats::contains).collect(Collectors.joining(", ")));
            }

            // Lưu booking
            Booking booking = new Booking();
            booking.setMovieId(bookingDTO.getMovieId());
            booking.setMovieTitle(bookingDTO.getMovieTitle());
            booking.setShowtime(bookingDTO.getShowtime());
            booking.setDate(bookingDate);
            booking.setTotal(bookingDTO.getTotal());
            booking.setUserId(bookingDTO.getUserId());

            Booking savedBooking;
            try {
                savedBooking = bookingRepository.saveAndFlush(booking);
                logger.info("Saved booking: {}", savedBooking);
            } catch (Exception e) {
                logger.error("Lỗi khi lưu booking: {}", bookingDTO, e);
                throw new RuntimeException("Không thể lưu booking: " + e.getMessage());
            }

            // Lưu ghế
            for (String seat : requestedSeats) {
                BookingSeat bookingSeat = new BookingSeat();
                bookingSeat.setBookingId(savedBooking.getId());
                bookingSeat.setSeat(seat);
                bookingSeat.setMovieId(bookingDTO.getMovieId());
                bookingSeat.setDate(bookingDate);
                bookingSeat.setShowtime(bookingDTO.getShowtime());
                try {
                    bookingSeatRepository.saveAndFlush(bookingSeat);
                    logger.info("Saved booking seat: seat={}, bookingId={}", seat, savedBooking.getId());
                } catch (Exception e) {
                    logger.error("Lỗi khi lưu booking seat: seat={}, bookingId={}", seat, savedBooking.getId(), e);
                    throw new RuntimeException("Không thể lưu ghế: " + seat + ", lỗi: " + e.getMessage());
                }
            }

            bookingDTO.setId(savedBooking.getId());
            bookingDTO.setCreatedAt(savedBooking.getCreatedAt());
            logger.info("Completed createBooking: {}", bookingDTO);
            return bookingDTO;
        } catch (Exception e) {
            logger.error("Lỗi trong createBooking: {}", bookingDTO, e);
            throw e;
        }
    }

    public List<BookingDTO> getBookingsByUserId(String userId) {
        logger.info("Fetching bookings for userId: {}", userId);
        try {
            List<Booking> bookings = bookingRepository.findByUserId(userId);
            return bookings.stream().map(booking -> {
                BookingDTO dto = new BookingDTO();
                dto.setId(booking.getId());
                dto.setMovieId(booking.getMovieId());
                dto.setMovieTitle(booking.getMovieTitle());
                dto.setShowtime(booking.getShowtime());
                dto.setDate(booking.getDate().toString());
                List<String> seats = bookingSeatRepository.findByBookingId(booking.getId()).stream()
                    .map(BookingSeat::getSeat)
                    .collect(Collectors.toList());
                dto.setSeats(seats);
                dto.setTotal(booking.getTotal());
                dto.setUserId(booking.getUserId());
                dto.setCreatedAt(booking.getCreatedAt());
                return dto;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Lỗi khi lấy bookings cho userId: {}", userId, e);
            throw new RuntimeException("Không thể lấy danh sách booking: " + e.getMessage());
        }
    }

    public List<String> getBookedSeats(String movieId, LocalDate date, String showtime) {
        logger.info("Fetching booked seats: movieId={}, date={}, showtime={}", movieId, date, showtime);
        try {
            return bookingSeatRepository.findSeatsByMovieIdAndDateAndShowtime(movieId, date, showtime);
        } catch (Exception e) {
            logger.error("Lỗi khi lấy booked seats: movieId={}, date={}, showtime={}", movieId, date, showtime, e);
            throw new RuntimeException("Không thể lấy danh sách ghế: " + e.getMessage());
        }
    }

    public List<MovieDTO> getBookedMovies(String userId) {
        logger.info("Fetching booked movies for userId: {}", userId);
        try {
            List<String> movieIds = bookingRepository.findDistinctMovieIdsByUserId(userId);
            return movieService.getMoviesByIds(movieIds);
        } catch (Exception e) {
            logger.error("Lỗi khi lấy booked movies cho userId: {}", userId, e);
            throw new RuntimeException("Không thể lấy danh sách phim: " + e.getMessage());
        }
    }
}