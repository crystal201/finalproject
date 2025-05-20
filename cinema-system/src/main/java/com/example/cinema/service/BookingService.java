package com.example.cinema.service;

import com.example.cinema.dto.BookingDTO;
import com.example.cinema.dto.MovieDTO;
import com.example.cinema.entity.Booking;
import com.example.cinema.entity.BookingSeat;
import com.example.cinema.repository.BookingRepository;
import com.example.cinema.repository.BookingSeatRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final BookingSeatRepository bookingSeatRepository;
    private final BookingSeatService bookingSeatService;

    public BookingService(BookingRepository bookingRepository, BookingSeatRepository bookingSeatRepository, BookingSeatService bookingSeatService) {
        this.bookingRepository = bookingRepository;
        this.bookingSeatRepository = bookingSeatRepository;
        this.bookingSeatService = bookingSeatService;
    }

    public BookingDTO createBooking(BookingDTO bookingDTO) {
        Booking booking = new Booking();
        booking.setMovieId(bookingDTO.getMovieId());
        booking.setMovieTitle(bookingDTO.getMovieTitle());
        booking.setShowtime(bookingDTO.getShowtime());
        booking.setDate(LocalDate.parse(bookingDTO.getDate()));
        booking.setTotal(bookingDTO.getTotal());
        booking.setUserId(bookingDTO.getUserId());
        booking.setRoomId(bookingDTO.getRoomId());
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
        return bookingDTO;
    }

    public List<BookingDTO> getBookingsByUserId(String userId) {
        return bookingRepository.findByUserId(userId).stream().map(booking -> {
            BookingDTO dto = new BookingDTO();
            dto.setId(booking.getId());
            dto.setMovieId(booking.getMovieId());
            dto.setMovieTitle(booking.getMovieTitle());
            dto.setShowtime(booking.getShowtime());
            dto.setDate(booking.getDate().toString());
            dto.setTotal(booking.getTotal());
            dto.setUserId(booking.getUserId());
            dto.setRoomId(booking.getRoomId());
            List<String> seats = bookingSeatRepository.findByBookingId(booking.getId())
                    .stream().map(BookingSeat::getSeat).collect(Collectors.toList());
            dto.setSeats(seats);
            return dto;
        }).collect(Collectors.toList());
    }

    public List<MovieDTO> getBookedMovies(String userId) {
        return bookingRepository.findDistinctMovieIdsByUserId(userId).stream().map(movieId -> {
            MovieDTO movie = new MovieDTO();
            movie.setId(movieId);
            return movie;
        }).collect(Collectors.toList());
    }

    public List<String> getBookedSeats(String movieId, LocalDate date, String showtime, Integer roomId) {
        return bookingSeatService.getBookedSeats(movieId, date, showtime, roomId);
    }
}