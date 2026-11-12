package com.ridex.driver;

import com.ridex.driver.dto.CreateVehicleRequest;
import com.ridex.driver.dto.DriverApplicationRequest;
import com.ridex.driver.dto.DriverResponse;
import com.ridex.driver.dto.VehicleResponse;
import com.ridex.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class DriverService {

    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;

    public DriverService(
            DriverRepository driverRepository,
            VehicleRepository vehicleRepository,
            UserRepository userRepository
    ){
        this.driverRepository = driverRepository;
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public DriverResponse apply(
            UUID userId,
            DriverApplicationRequest request
    ){
        if(!userRepository.existsById(userId)){
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "User account was not found"
            );
        }

        if(driverRepository.existsById(userId)){
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A driver application already exists"
            );
        }

        Driver driver = new Driver();
        driver.setUserId(userId);
        driver.setLicenceNumber(request.licenceNumber());

        Driver savedDriver = driverRepository.save(driver);

        return DriverResponse.from(savedDriver, List.of());
    }

    @Transactional
    public VehicleResponse addVehicle(
            UUID userId,
            CreateVehicleRequest request
    ){
        Driver driver = findDriver(userId);

        if(driver.getDriverStatus() == DriverStatus.REJECTED
              || driver.getDriverStatus() == DriverStatus.BLOCKED
              || driver.getDriverStatus() == DriverStatus.SUSPENDED){
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "This driver account cannot add vehicles"
            );
        }

        if(vehicleRepository.existsByRegistrationNumber(
                request.registrationNumber()
        )){
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A vehicle with this registration number already exists"
            );
        }

        Vehicle vehicle = new Vehicle();
        vehicle.setDriverId(userId);
        vehicle.setRegistrationNumber(request.registrationNumber());
        vehicle.setVehicleType(request.vehicleType());
        vehicle.setMake(request.make());
        vehicle.setModel(request.model());
        vehicle.setColour(request.colour());
        vehicle.setManufactureYear(request.manufactureYear());
        vehicle.setCapacity(request.capacity());

        return VehicleResponse.from(vehicleRepository.save(vehicle));
    }

    @Transactional(readOnly = true)
    public DriverResponse getMyDriverProfile(UUID userId){
        Driver driver = findDriver(userId);

        List<VehicleResponse> vehicles = vehicleRepository
                .findByDriverIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(VehicleResponse::from)
                .toList();

        return DriverResponse.from(driver, vehicles);
    }

    private Driver findDriver(UUID userId){
        return driverRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Driver profile was not found"
                ));
    }
}
