package com.ridex.driver;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "drivers")
@Getter
@Setter
@NoArgsConstructor
public class Driver {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "licence_number", nullable = false, unique = true, length = 100)
    private String licenceNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "driver_status", nullable = false)
    private DriverStatus driverStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "availability_status", nullable = false)
    private AvailabilityStatus availabilityStatus;

    @Column(name = "rating_average", nullable = false)
    private BigDecimal ratingAverage;

    @Column(name = "rating_count", nullable = false)
    private int ratingCount;

    @Column(name = "approved_at")
    private Instant approvedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void onCreate(){
        Instant now = Instant.now();
        driverStatus = DriverStatus.PENDING_VERIFICATION;
        availabilityStatus = AvailabilityStatus.OFFLINE;
        ratingAverage = BigDecimal.ZERO;
        ratingCount = 0;
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate(){
        updatedAt = Instant.now();
    }


}
