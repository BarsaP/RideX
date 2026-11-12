package com.ridex.driver.dto;

import com.ridex.driver.Vehicle;
import com.ridex.driver.VehicleType;
import com.ridex.driver.VehicleVerificationStatus;

import java.util.UUID;

public record VehicleResponse(
        UUID id,
        String registrationNumber,
        VehicleType vehicleType,
        String make,
        String model,
        String colour,
        int manufactureYear,
        int capacity,
        VehicleVerificationStatus verificationStatus,
        boolean active
) {
    public static VehicleResponse from(Vehicle vehicle){
        return new VehicleResponse(
                vehicle.getId(),
                vehicle.getRegistrationNumber(),
                vehicle.getVehicleType(),
                vehicle.getMake(),
                vehicle.getModel(),
                vehicle.getColour(),
                vehicle.getManufactureYear(),
                vehicle.getCapacity(),
                vehicle.getVerificationStatus(),
                vehicle.isActive()
        );
    }
}
