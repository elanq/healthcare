package com.fastcampus.healthcare.controller;

import com.fastcampus.healthcare.model.AppointmentRequest;
import com.fastcampus.healthcare.model.AppointmentRescheduleRequest;
import com.fastcampus.healthcare.model.AppointmentResponse;
import com.fastcampus.healthcare.model.UserInfo;
import com.fastcampus.healthcare.service.AppointmentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

  @PutMapping("{appointmentId}/reschedule")
  public ResponseEntity<AppointmentResponse> reschedule(
      @PathVariable Long  appointmentId,
      @Valid @RequestBody AppointmentRescheduleRequest request
  ) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    UserInfo userInfo = (UserInfo) authentication.getPrincipal();

    AppointmentResponse response = appointmentService.rescheduleAppointment(userInfo.getUserId(), appointmentId, request);

    return ResponseEntity.ok(response);
  }

  @PutMapping("{appointmentId}/cancel")
  public ResponseEntity<AppointmentResponse> cancelAppointment(
      @PathVariable Long appointmentId
  ) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    UserInfo userInfo = (UserInfo) authentication.getPrincipal();

    appointmentService.cancelAppointment(userInfo.getUserId(), appointmentId);

    AppointmentResponse response = appointmentService.findById(appointmentId);

    return ResponseEntity.ok(response);
  }

  @GetMapping("")
  public ResponseEntity<List<AppointmentResponse>> listAppointments(
  ) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    UserInfo userInfo = (UserInfo) authentication.getPrincipal();

    List<AppointmentResponse>  appointmentResponses = appointmentService.listUserAppointments(userInfo.getUserId());

    return ResponseEntity.ok(appointmentResponses);
  }

  @GetMapping("/{appointmentId}")
  public ResponseEntity<AppointmentResponse> getAppointment(
      @PathVariable Long appointmentId
  ) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    UserInfo userInfo = (UserInfo) authentication.getPrincipal();

    AppointmentResponse  appointmentResponse = appointmentService.findById(appointmentId);

    return ResponseEntity.ok(appointmentResponse);
  }

}
