package com.fastcampus.healthcare.model;

import com.fastcampus.healthcare.common.constant.RoleType;
import com.fastcampus.healthcare.entity.Role;
import com.fastcampus.healthcare.entity.User;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserResponse {
  private Long userId;
  private String username;
  private String email;
  private boolean enabled;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private List<RoleType> roles;

  public static UserResponse fromUserAndRoles(User user, List<Role> roles) {
    return UserResponse.builder()
        .userId(user.getUserId())
        .username(user.getUsername())
        .email(user.getEmail())
        .enabled(user.isEnabled())
        .createdAt(user.getCreatedAt())
        .updatedAt(user.getUpdatedAt())
        .roles(roles.stream()
            .map(Role::getName)
            .toList())
        .build();
  }
}
