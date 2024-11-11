package com.fastcampus.healthcare.controller;

import com.fastcampus.healthcare.common.exception.ForbiddenAccessException;
import com.fastcampus.healthcare.entity.Doctor;
import com.fastcampus.healthcare.model.DoctorAvailabilityRequest;
import com.fastcampus.healthcare.model.DoctorResponse;
import com.fastcampus.healthcare.model.UserInfo;
import com.fastcampus.healthcare.service.DoctorService;
import io.micrometer.core.instrument.MeterRegistry;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/doctors")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer")
public class DoctorController {
  private final DoctorService doctorService;
  private final MeterRegistry meterRegistry;

  @GetMapping
  public ResponseEntity<Page<DoctorResponse>> searchDoctors(
      @RequestParam(required = false) String keyword,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "name") String sortBy,
      @RequestParam(defaultValue = "asc") String sortDir) {

    Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
    PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sortBy));

    meterRegistry.counter("search.count")
        .increment();

    Page<DoctorResponse> doctors = doctorService.getAllDoctors(keyword, pageRequest);
    return ResponseEntity.ok(doctors);
  }

  @GetMapping("/{id}")
  public ResponseEntity<DoctorResponse> getHospitalById(@PathVariable Long id) {
    DoctorResponse doctorResponse = doctorService.getDoctorById(id);
    return ResponseEntity.ok(doctorResponse);
  }

  @PreAuthorize("hasRole('DOCTOR')")
  @PostMapping("/{doctorId}/availabilities")
  public ResponseEntity<DoctorResponse> updateDoctorAvailability(
      @PathVariable Long doctorId,
      @Valid @RequestBody DoctorAvailabilityRequest request) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    UserInfo userInfo = (UserInfo) authentication.getPrincipal();

    Doctor existingDoctor = doctorService.getDoctorByUserId(userInfo.getUserId());
    if (!existingDoctor.getUserId().equals(userInfo.getUserId())) {
      throw new ForbiddenAccessException("Cannot update doctor availability");
    }
    DoctorResponse response = doctorService.updateDoctorAvailability(existingDoctor.getId(), request);
    return ResponseEntity.ok(response);
  }

  @PreAuthorize("hasRole('DOCTOR')")
  @DeleteMapping("/availabilities/{availabilityId}")
  public ResponseEntity<Void> deleteDoctorAvailability(@PathVariable Long availabilityId) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    UserInfo userInfo = (UserInfo) authentication.getPrincipal();

    Doctor existingDoctor = doctorService.getDoctorByUserId(userInfo.getUserId());
    doctorService.deleteDoctorAvailability(existingDoctor.getId(), availabilityId);
    return ResponseEntity.noContent().build();
  }
}
