package com.fastcampus.healthcare.model;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentRequest {
  private Long userId;
  @NotNull(message = "Doctor ID is required")
  private Long doctorId;

  @NotNull(message = "Doctor Specialization ID is required")
  private Long doctorSpecializationId;

  @NotNull(message = "Appointment date is required")
  private LocalDate appointmentDate;

  @NotNull(message = "Start time is required")
  private LocalTime startTime;

  @NotNull(message = "End time is required")
  private LocalTime endTime;}
