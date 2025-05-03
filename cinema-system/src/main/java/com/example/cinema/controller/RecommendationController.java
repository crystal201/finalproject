package com.example.cinema.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping
    public ResponseEntity<List<Object>> getRecommendations(@RequestParam("user_id") int userId) {
        String flaskUrl = "http://recommendation-api:5000/recommendations?user_id=" + userId;
        ResponseEntity<List> response = restTemplate.getForEntity(flaskUrl, List.class);
        return ResponseEntity.ok(response.getBody());
    }
}