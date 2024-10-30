package com.fastcampus.healthcare.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(SnakeCaseStrategy.class)
public class HospitalRequest {
  @NotBlank(message = "name is required")
  private String name;
  @NotBlank(message = "Address is required")
  private String address;
  @NotBlank(message = "Phone number is required")
  @Pattern(regexp = "^\\+?[0-9]{10,14}$", message = "Invalid phone number")
  private String phone;
  @NotBlank(message = "Email is required")
  @Email(message = "Invalid email format")
  private String email;
  @NotBlank(message = "Description is required")
  private String description;
}
