package com.fastcampus.healthcare.service;

import com.fastcampus.healthcare.entity.Doctor;
import com.fastcampus.healthcare.entity.DoctorAvailability;
import com.fastcampus.healthcare.model.DoctorAvailabilityRequest;
import com.fastcampus.healthcare.model.DoctorRegistrationRequest;
import com.fastcampus.healthcare.model.DoctorResponse;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DoctorService {
  DoctorResponse registerDoctor(DoctorRegistrationRequest request);
  Page<DoctorResponse> getAllDoctors(String keyword, Pageable pageable);
  DoctorResponse getDoctorById(Long doctorId);
  Doctor getDoctorByUserId(Long userId);
  DoctorResponse addDoctorSpecialization(Long doctorId, Long specializationId, BigDecimal fee, String consultationType);
  void deleteDoctorAvailability(Long doctorId, Long availabilityId);
  List<DoctorAvailability> getDoctorAvailabilitiesFromToday(Long doctorId);
  DoctorResponse updateDoctorAvailability(Long doctorId, DoctorAvailabilityRequest request);
}
