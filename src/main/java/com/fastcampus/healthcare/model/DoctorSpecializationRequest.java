package com.fastcampus.healthcare.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(SnakeCaseStrategy.class)
public class DoctorSpecializationRequest {
  @NotNull(message = "Specialization ID is required")
  @Positive(message = "Specialization ID must be positive")
  private Long specializationId;

  @NotNull(message = "Fee is required")
  @Positive(message = "Fee must be positive")
  private BigDecimal baseFee;

  @NotNull(message = "Consultation type is required")
  @Pattern(regexp = "^(ONLINE|OFFLINE)$", message = "Consultation type must be either ONLINE or OFFLINE")
  private String consultationType;
}
