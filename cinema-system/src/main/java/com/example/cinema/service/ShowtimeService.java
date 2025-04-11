package com.example.cinema.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.cinema.entity.Showtime;
import com.example.cinema.repository.ShowtimeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ShowtimeService {
    private final ShowtimeRepository showtimeRepository;

    public List<Showtime> getAllShowtimes() {
        return showtimeRepository.findAll();
    }

    public Optional<Showtime> getShowtimeById(Long id) {
        return showtimeRepository.findById(id);
    }

    public Showtime addShowtime(Showtime showtime) {
        return showtimeRepository.save(showtime);
    }
}

