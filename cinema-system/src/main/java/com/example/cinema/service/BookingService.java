package com.example.cinema.service;

import com.example.cinema.dto.BookingDTO;
import com.example.cinema.dto.MovieDTO;
import com.example.cinema.entity.Booking;
import com.example.cinema.repository.BookingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;    
    private final MovieService movieService;
    public BookingDTO createBooking(BookingDTO bookingDTO) {
        Booking booking = new Booking();
        booking.setMovieId(bookingDTO.getMovieId());
        booking.setMovieTitle(bookingDTO.getMovieTitle());
        booking.setShowtime(bookingDTO.getShowtime());
        booking.setDate(bookingDTO.getDate());
        booking.setSeats(bookingDTO.getSeats());
        booking.setTotal(bookingDTO.getTotal());
        booking.setUserId(bookingDTO.getUserId());

        Booking savedBooking = bookingRepository.save(booking);

        bookingDTO.setId(savedBooking.getId());
        bookingDTO.setCreatedAt(savedBooking.getCreatedAt());
        return bookingDTO;
    }

    public List<BookingDTO> getBookingsByUserId(String userId) {
        return bookingRepository.findByUserId(userId).stream().map(booking -> {
            BookingDTO dto = new BookingDTO();
            dto.setId(booking.getId());
            dto.setMovieId(booking.getMovieId());
            dto.setMovieTitle(booking.getMovieTitle());
            dto.setShowtime(booking.getShowtime());
            dto.setDate(booking.getDate());
            dto.setSeats(booking.getSeats());
            dto.setTotal(booking.getTotal());
            dto.setUserId(booking.getUserId());
            dto.setCreatedAt(booking.getCreatedAt());
            return dto;
        }).collect(Collectors.toList());
    }

    public BookingService(BookingRepository bookingRepository, MovieService movieService) {
        this.bookingRepository = bookingRepository;
        this.movieService = movieService;
    }

    public List<MovieDTO> getBookedMovies(String userId) {
        List<String> movieIds = bookingRepository.findDistinctMovieIdsByUserId(userId);
        return movieService.getMoviesByIds(movieIds);
    }
}