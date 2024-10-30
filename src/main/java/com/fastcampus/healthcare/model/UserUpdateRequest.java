package com.fastcampus.healthcare.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateRequest {
  @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
  private String username;

  @Email(message = "Invalid email format")
  private String email;

  @Size(min = 8, max = 50, message = "Password must be between 8 and 50 characters")
  @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
      message = "Password must contain at least one digit, one lowercase, one uppercase, one special character, and no whitespace")
  private String newPassword;

  private String currentPassword;
}
