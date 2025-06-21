package com.example.cinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.cinema.entity.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {
}