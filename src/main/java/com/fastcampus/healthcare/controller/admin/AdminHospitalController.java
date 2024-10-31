package com.fastcampus.healthcare.controller.admin;

import com.fastcampus.healthcare.model.HospitalRequest;
import com.fastcampus.healthcare.model.HospitalResponse;
import com.fastcampus.healthcare.service.HospitalService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/hospitals")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer")
@PreAuthorize("hasRole('SUPER_ADMIN')")

public class AdminHospitalController {

  private final HospitalService hospitalService;

  @PostMapping
  public ResponseEntity<HospitalResponse> registerHospital(
      @Valid @RequestBody HospitalRequest hospital) {
    HospitalResponse registeredHospital = hospitalService.create(hospital);
    return ResponseEntity.status(HttpStatus.CREATED).body(registeredHospital);
  }

  @PutMapping("/{id}")
  public ResponseEntity<HospitalResponse> updateHospital(
      @PathVariable Long id,
      @Valid @RequestBody HospitalRequest hospitalRequest) {
    HospitalResponse updatedHospital = hospitalService.update(id, hospitalRequest);
    return ResponseEntity.ok(updatedHospital);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteHospital(@PathVariable Long id) {
    hospitalService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
