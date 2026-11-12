CREATE TABLE vehicles(
    id UUID PRIMARY KEY,
    driver_id UUID NOT NULL REFERENCES drivers(user_id) ON DELETE CASCADE,
    registration_number VARCHAR(30) NOT NULL UNIQUE,
    vehicle_type VARCHAR(20) NOT NULL,
    make VARCHAR(100) NOT NULL,
    model VARCHAR(100) NOT NULL,
    colour VARCHAR(50) NOT NULL,
    manufacture_year INTEGER NOT NULL,
    capacity INTEGER NOT NULL,
    verification_status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_vehicle_type CHECK (
        vehicle_type IN ('BIKE', 'AUTO', 'SEDAN', 'SUV')
        ),

    CONSTRAINT chk_vehicle_verification CHECK (
        verification_status IN ('PENDING', 'VERIFIED', 'REJECTED')
        ),

    CONSTRAINT chk_vehicle_capacity CHECK ( capacity > 0 )
);