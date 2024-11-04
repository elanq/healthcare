CREATE TABLE appointment (
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    hospital_id BIGINT NOT NULL,
    doctor_specialization_id BIGINT NOT NULL,
    appointment_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    consultation_type VARCHAR(10) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_patient FOREIGN KEY (patient_id) REFERENCES users(user_id),
    CONSTRAINT fk_doctor FOREIGN KEY (doctor_id) REFERENCES doctor(id),
    CONSTRAINT fk_hospital FOREIGN KEY (hospital_id) REFERENCES hospital(id)
);

CREATE INDEX idx_appointment_patient ON appointment(patient_id);
CREATE INDEX idx_appointment_doctor ON appointment(doctor_id);
CREATE INDEX idx_appointment_hospital ON appointment(hospital_id);
CREATE INDEX idx_appointment_date ON appointment(appointment_date);