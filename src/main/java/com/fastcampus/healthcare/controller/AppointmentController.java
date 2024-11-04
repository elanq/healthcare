package com.fastcampus.healthcare.controller;

import com.fastcampus.healthcare.model.AppointmentRequest;
import com.fastcampus.healthcare.model.AppointmentResponse;
import com.fastcampus.healthcare.service.AppointmentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/appointments")
@SecurityRequirement(name = "Bearer")
public class AppointmentController {
  private final AppointmentService appointmentService;

  @PostMapping("/book")
  public ResponseEntity<AppointmentResponse> bookAppointment(@Valid @RequestBody AppointmentRequest request) {
    AppointmentResponse response = appointmentService.bookAppointment(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }
}
