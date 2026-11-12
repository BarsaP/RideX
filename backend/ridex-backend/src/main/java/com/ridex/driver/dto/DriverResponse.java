package com.ridex.driver.dto;

import com.ridex.driver.AvailabilityStatus;
import com.ridex.driver.Driver;
import com.ridex.driver.DriverStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record DriverResponse(
        UUID userId,
        String licenceNumber,
        DriverStatus driverStatus,
        AvailabilityStatus availabilityStatus,
        BigDecimal ratingAverage,
        int ratingCount,
        List<VehicleResponse> vehicles
) {
    public static DriverResponse from(
            Driver driver,
            List<VehicleResponse> vehicles
    ){
        return new DriverResponse(
                driver.getUserId(),
                driver.getLicenceNumber(),
                driver.getDriverStatus(),
                driver.getAvailabilityStatus(),
                driver.getRatingAverage(),
                driver.getRatingCount(),
                vehicles
        );
    }
}
