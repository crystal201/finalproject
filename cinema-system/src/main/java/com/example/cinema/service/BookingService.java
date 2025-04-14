package com.example.cinema.service;

import com.example.cinema.dto.BookingDTO;
import com.example.cinema.entity.Booking;
import com.example.cinema.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {
    @Autowired
    private BookingRepository bookingRepository;

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
            return dto;
        }).collect(Collectors.toList());
    }
}