package com.ridex.driver;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {

    boolean existsByRegistrationNumber(String registrationNumber);

    List<Vehicle> findByDriverIdOrderByCreatedAtDesc(UUID driverId);
}
