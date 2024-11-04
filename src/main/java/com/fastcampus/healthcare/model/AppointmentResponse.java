package com.fastcampus.healthcare.model;

import com.fastcampus.healthcare.common.constant.AppointmentStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(SnakeCaseStrategy.class)
@JsonInclude(Include.NON_NULL)
public class AppointmentResponse {
  private Long id;
  private Long patientId;
  private String patientName;
  private Long doctorId;
  private String doctorName;
  private Long hospitalId;
  private String hospitalName;
  private LocalDate appointmentDate;
  private LocalTime startTime;
  private LocalTime endTime;
  private String consultationType;
  private AppointmentStatus status;
}