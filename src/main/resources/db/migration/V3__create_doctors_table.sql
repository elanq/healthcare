-- Create SPECIALIZATION table
CREATE TABLE specialization (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create DOCTOR table
CREATE TABLE doctor (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    hospital_id BIGINT NOT NULL,
    bio TEXT,
name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (hospital_id) REFERENCES hospital(id)
);

-- Create DOCTOR_SPECIALIZATION table
CREATE TABLE doctor_specialization (
    id BIGSERIAL PRIMARY KEY,
    doctor_id BIGINT NOT NULL,
    specialization_id BIGINT NOT NULL,
    base_fee DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (doctor_id) REFERENCES doctor(id),
    FOREIGN KEY (specialization_id) REFERENCES specialization(id),
    UNIQUE (doctor_id, specialization_id)
);

-- Create HOSPITAL_DOCTOR_FEE table
CREATE TABLE hospital_doctor_fee (
    id BIGSERIAL PRIMARY KEY,
    hospital_id BIGINT NOT NULL,
    doctor_specialization_id BIGINT NOT NULL,
    fee DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (hospital_id) REFERENCES hospital(id),
    FOREIGN KEY (doctor_specialization_id) REFERENCES doctor_specialization(id),
    UNIQUE (hospital_id, doctor_specialization_id)
);

-- Create DOCTOR_AVAILABILITY table
CREATE TABLE doctor_availability (
    id BIGSERIAL PRIMARY KEY,
    doctor_id BIGINT NOT NULL,
    date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    consultation_type VARCHAR(50) NOT NULL,
    is_available BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (doctor_id) REFERENCES doctor(id)
);

CREATE INDEX idx_doctor_user_id ON doctor(user_id);
CREATE INDEX idx_doctor_hospital_id ON doctor(hospital_id);

CREATE INDEX idx_doctor_specialization_doctor_id ON doctor_specialization(doctor_id);
CREATE INDEX idx_doctor_specialization_specialization_id ON doctor_specialization(specialization_id);

CREATE INDEX idx_hospital_doctor_fee_hospital_id ON hospital_doctor_fee(hospital_id);
CREATE INDEX idx_hospital_doctor_fee_doctor_specialization_id ON hospital_doctor_fee(doctor_specialization_id);

CREATE INDEX idx_doctor_availability_doctor_id ON doctor_availability(doctor_id);
CREATE INDEX idx_doctor_availability_date ON doctor_availability(date);

