package com.example.cinema.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.cinema.entity.FoodDrink;
import com.example.cinema.repository.FoodDrinkRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FoodDrinkService {
    private final FoodDrinkRepository foodDrinkRepository;

    public List<FoodDrink> getAllItems() {
        return foodDrinkRepository.findAll();
    }

    public FoodDrink addItem(FoodDrink item) {
        return foodDrinkRepository.save(item);
    }
}

