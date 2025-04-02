package com.koli4ka.app.car.repository;

import com.koli4ka.app.car.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface CarRepository extends JpaRepository<Car, UUID> {
    List<Car> findByPublishedAtBefore(LocalDateTime cutoffDate);
}
