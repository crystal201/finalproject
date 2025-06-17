package com.example.admin.controller;

import com.example.admin.entity.Room;
import com.example.admin.entity.BookingCancellation;
import com.example.admin.repository.RoomRepository;
import com.example.admin.repository.BookingCancellationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private RoomRepository roomRepo;

    @Autowired
    private BookingCancellationRepository cancelRepo;

    @GetMapping("/rooms")
    public List<Room> getRooms() {
        return roomRepo.findAll();
    }

    @PostMapping("/rooms")
    public Room addRoom(@RequestBody Room room) {
        return roomRepo.save(room);
    }

    @DeleteMapping("/rooms/{id}")
    public void deleteRoom(@PathVariable Long id) {
        roomRepo.deleteById(id);
    }

    @PutMapping("/cancellations/{id}/approve")
    public BookingCancellation approveCancellation(@PathVariable Long id) {
        BookingCancellation cancel = cancelRepo.findById(id).orElseThrow();
        cancel.setStatus("APPROVED");
        return cancelRepo.save(cancel);
    }
}