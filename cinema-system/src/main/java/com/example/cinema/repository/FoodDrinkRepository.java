package com.example.cinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.cinema.entity.FoodDrink;

@Repository
public interface FoodDrinkRepository extends JpaRepository<FoodDrink, Long> {
}

