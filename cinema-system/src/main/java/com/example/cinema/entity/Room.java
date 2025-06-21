package com.example.cinema.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Room {
    @Id
    private Long id;
    private Integer capacity;
    private String roomName;
    private String status;

    // Getters, setters, constructors
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
