package com.ridex.driver.dto;

import com.ridex.driver.VehicleType;
import jakarta.validation.constraints.*;

public record CreateVehicleRequest (

        @NotBlank(message = "Registration number is required")
        @Size(max = 30, message = "Registration number must not exceed 30 characters")
        String registrationNumber,

        @NotNull(message = "Vehicle type is required")
        VehicleType vehicleType,

        @NotNull(message = "Vehicle make is required")
        String make,

        @NotNull(message = "Vehicle model is required")
        String model,

        @NotNull(message = "Vehicle colour is required")
        String colour,

        @Min(value = 1900, message = "Manufacture year is invalid")
        @Max(value = 2100, message = "Manufacture year is invalid")
        int manufactureYear,

        @Min(value = 1, message = "Vehicle capacity must be at least 1")
        int capacity
){
}
