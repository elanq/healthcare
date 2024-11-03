package com.fastcampus.healthcare.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(SnakeCaseStrategy.class)
public class DoctorResponse {
  private Long id;
  private Long userId;
  private String name;
  private String email;
  private Long hospitalId;
  private String hospitalName;
  private String bio;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private List<SpecializationInfo> specializations;
  private List<AvailabilityInfo> availabilities;
}
