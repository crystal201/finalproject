package com.example.demo.repository;

import com.example.demo.entity.BookingCancellation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingCancellationRepository extends JpaRepository<BookingCancellation, Long> {
}