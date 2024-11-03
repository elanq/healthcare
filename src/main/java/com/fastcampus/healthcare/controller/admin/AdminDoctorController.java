package com.fastcampus.healthcare.controller.admin;

import com.fastcampus.healthcare.model.DoctorRegistrationRequest;
import com.fastcampus.healthcare.model.DoctorResponse;
import com.fastcampus.healthcare.model.DoctorSpecializationRequest;
import com.fastcampus.healthcare.model.GrantUserRoleRequest;
import com.fastcampus.healthcare.model.UserResponse;
import com.fastcampus.healthcare.service.DoctorService;
import com.fastcampus.healthcare.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/doctors")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer")
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HOSPITAL_ADMIN')")
public class AdminDoctorController {
  private final DoctorService doctorService;

  @PostMapping("/register")
  public ResponseEntity<DoctorResponse> registerDoctor(@Valid @RequestBody DoctorRegistrationRequest request) {
    DoctorResponse response = doctorService.registerDoctor(request);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/{doctorId}/specializations")
  public ResponseEntity<DoctorResponse> addDoctorSpecialization(
      @PathVariable Long doctorId,
      @Valid @RequestBody DoctorSpecializationRequest request) {
    DoctorResponse response = doctorService.addDoctorSpecialization(
        doctorId,
        request.getSpecializationId(),
        request.getBaseFee(),
        request.getConsultationType()
    );
    return ResponseEntity.ok(response);
  }
}
