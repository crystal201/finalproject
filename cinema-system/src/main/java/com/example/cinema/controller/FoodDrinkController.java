package com.example.cinema.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.cinema.entity.FoodDrink;
import com.example.cinema.service.FoodDrinkService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/food-drinks")
@RequiredArgsConstructor
public class FoodDrinkController {
    private final FoodDrinkService foodDrinkService;

    @GetMapping
    public List<FoodDrink> getAllItems() {
        return foodDrinkService.getAllItems();
    }

    @PostMapping
    public ResponseEntity<FoodDrink> addItem(@RequestBody FoodDrink item) {
        return ResponseEntity.ok(foodDrinkService.addItem(item));
    }
}
