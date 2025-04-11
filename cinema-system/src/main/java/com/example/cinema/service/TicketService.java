package com.example.cinema.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.cinema.entity.Ticket;
import com.example.cinema.repository.TicketRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketService {
    private final TicketRepository ticketRepository;

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public Ticket bookTicket(Ticket ticket) {
        return ticketRepository.save(ticket);
    }
}
