package com.ridex.driver.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DriverApplicationRequest (

    @NotBlank(message = "Driving licence number is required")
    @Size(max = 100, message = "Driving licence number must not exceed 100 characters")
    String licenceNumber){
}
