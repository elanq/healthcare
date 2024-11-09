package com.fastcampus.healthcare.model;

import com.fastcampus.healthcare.common.constant.AppointmentStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
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
public class AppointmentMeetingResponse {
  private Long patientId;
  private Long doctorId;
  private AppointmentStatus status;
  private String meetingId;
}
