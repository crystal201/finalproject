package com.example.cinema.service;

import com.example.cinema.entity.BookingSeat;
import com.example.cinema.repository.BookingSeatRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingSeatService {
    private static final Logger logger = LoggerFactory.getLogger(BookingSeatService.class);
    private final BookingSeatRepository bookingSeatRepository;

    public BookingSeatService(BookingSeatRepository bookingSeatRepository) {
        this.bookingSeatRepository = bookingSeatRepository;
    }

    public List<String> getBookedSeats(String movieId, LocalDate date, String showtime) {
        try {
            return bookingSeatRepository.findSeatsByMovieIdAndDateAndShowtime(movieId, date, showtime);
        } catch (Exception e) {
            logger.error("Lỗi khi lấy ghế đã đặt: movieId={}, date={}, showtime={}", movieId, date, showtime, e);
            throw new RuntimeException("Không thể lấy danh sách ghế: " + e.getMessage());
        }
    }

    public void saveSeats(Long bookingId, List<String> seats, String movieId, LocalDate date, String showtime) {
        try {
            List<String> bookedSeats = getBookedSeats(movieId, date, showtime);
            if (seats.stream().anyMatch(bookedSeats::contains)) {
                throw new RuntimeException("Một số ghế đã được đặt: " + seats.stream().filter(bookedSeats::contains).collect(Collectors.joining(", ")));
            }

            for (String seat : seats) {
                BookingSeat bookingSeat = new BookingSeat();
                bookingSeat.setBookingId(bookingId);
                bookingSeat.setSeat(seat);
                bookingSeat.setMovieId(movieId);
                bookingSeat.setDate(date);
                bookingSeat.setShowtime(showtime);
                bookingSeatRepository.save(bookingSeat);
            }
        } catch (Exception e) {
            logger.error("Lỗi khi lưu ghế: bookingId={}, seats={}", bookingId, seats, e);
            throw new RuntimeException("Không thể lưu ghế: " + e.getMessage());
        }
    }

    public List<String> getSeatsByBookingId(Long bookingId) {
        try {
            return bookingSeatRepository.findByBookingId(bookingId).stream()
                    .map(BookingSeat::getSeat)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Lỗi khi lấy ghế theo bookingId={}", bookingId, e);
            throw new RuntimeException("Không thể lấy ghế: " + e.getMessage());
        }
    }
}