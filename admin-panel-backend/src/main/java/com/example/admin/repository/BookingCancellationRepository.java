package com.example.admin.repository;

import com.example.admin.entity.BookingCancellation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingCancellationRepository extends JpaRepository<BookingCancellation, Long> {
}