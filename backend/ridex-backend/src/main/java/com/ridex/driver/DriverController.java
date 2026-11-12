package com.ridex.driver;

import com.ridex.driver.dto.CreateVehicleRequest;
import com.ridex.driver.dto.DriverApplicationRequest;
import com.ridex.driver.dto.DriverResponse;
import com.ridex.driver.dto.VehicleResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/drivers")
public class DriverController {

    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    @PostMapping("/apply")
    public ResponseEntity<DriverResponse> apply(
            @AuthenticationPrincipal UUID userId,
            @Valid @RequestBody DriverApplicationRequest request
    ){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(driverService.apply(userId, request));
    }

    @PostMapping("/me/vehicles")
    public ResponseEntity<VehicleResponse> addVehicle(
            @AuthenticationPrincipal UUID userId,
            @Valid @RequestBody CreateVehicleRequest request
            ){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(driverService.addVehicle(userId, request));
    }

    @GetMapping("/me")
    public ResponseEntity<DriverResponse> getMyDriverProfile(
            @AuthenticationPrincipal UUID userId
    ){
        return ResponseEntity.ok(
                driverService.getMyDriverProfile(userId)
        );
    }
}
