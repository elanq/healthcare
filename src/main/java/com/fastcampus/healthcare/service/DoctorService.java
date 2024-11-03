package com.fastcampus.healthcare.service;

import com.fastcampus.healthcare.model.DoctorRegistrationRequest;
import com.fastcampus.healthcare.model.DoctorResponse;
import java.math.BigDecimal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DoctorService {
  DoctorResponse registerDoctor(DoctorRegistrationRequest request);
  Page<DoctorResponse> getAllDoctors(String keyword, Pageable pageable);
  DoctorResponse getDoctorById(Long doctorId);
  DoctorResponse addDoctorSpecialization(Long doctorId, Long specializationId, BigDecimal fee, String consultationType);
}
