package com.example.cinema.service;

import com.example.cinema.dto.MovieDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MovieService {

    @Value("${tmdb.api.key}")
    private String tmdbApiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public MovieService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public MovieDTO getMovieDetails(String movieId) {
        try {
            String url = String.format(
                "https://api.themoviedb.org/3/movie/%s?api_key=%s",
                movieId, tmdbApiKey
            );
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            return objectMapper.convertValue(response, MovieDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy thông tin phim từ TMDB: " + movieId, e);
        }
    }

    public List<MovieDTO> getMoviesByIds(List<String> movieIds) {
        return movieIds.stream()
            .map(this::getMovieDetails)
            .collect(Collectors.toList());
    }
}