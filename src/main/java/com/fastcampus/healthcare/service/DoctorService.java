package com.fastcampus.healthcare.service;

import com.fastcampus.healthcare.model.DoctorRegistrationRequest;
import com.fastcampus.healthcare.model.DoctorResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DoctorService {
  DoctorResponse registerDoctor(DoctorRegistrationRequest request);
  Page<DoctorResponse> getAllDoctors(String keyword, Pageable pageable);
  DoctorResponse getDoctorById(Long doctorId);
}
