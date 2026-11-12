CREATE TABLE drivers (
                         user_id UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
                         licence_number VARCHAR(100) NOT NULL UNIQUE,
                         driver_status VARCHAR(30) NOT NULL DEFAULT 'PENDING_VERIFICATION',
                         availability_status VARCHAR(30) NOT NULL DEFAULT 'OFFLINE',
                         rating_average NUMERIC(3,2) NOT NULL DEFAULT 0,
                         rating_count INTEGER NOT NULL DEFAULT 0,
                         approved_at TIMESTAMPTZ,
                         created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

                         CONSTRAINT chk_driver_status CHECK (
                             driver_status IN (
                                               'PENDING_VERIFICATION',
                                               'ACTIVE',
                                               'SUSPENDED',
                                               'REJECTED',
                                               'BLOCKED'
                                 )
                             ),

                         CONSTRAINT chk_driver_availability CHECK (
                             availability_status IN (
                                                     'OFFLINE',
                                                     'AVAILABLE',
                                                     'ASSIGNED',
                                                     'ARRIVING',
                                                     'ON_TRIP'
                                 )
                             )
);