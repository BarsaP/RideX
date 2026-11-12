package com.ridex.driver;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "vehicles")
@Getter
@Setter
@NoArgsConstructor
public class Vehicle {

    @Id
    private UUID id;

    @Column(name = "driver_id", nullable = false)
    private UUID driverId;

    @Column(name = "registration_number", nullable = false, unique = true, length = 30)
    private String registrationNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", nullable = false)
    private VehicleType vehicleType;

    @Column(nullable = false, length = 100)
    private String make;

    @Column(nullable = false, length = 100)
    private String model;

    @Column(name = "colour", nullable = false, length = 50)
    private String colour;

    @Column(name = "manufacture_year", nullable = false)
    private int manufactureYear;

    @Column(nullable = false)
    private int capacity;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false)
    private VehicleVerificationStatus verificationStatus;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void onCreate(){
        Instant now = Instant.now();
        id = UUID.randomUUID();
        verificationStatus = VehicleVerificationStatus.PENDING;
        active = true;
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate(){
        updatedAt = Instant.now();
    }
}
