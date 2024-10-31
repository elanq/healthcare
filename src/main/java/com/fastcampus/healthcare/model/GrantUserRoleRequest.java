package com.fastcampus.healthcare.model;

import com.fastcampus.healthcare.common.constant.RoleType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GrantUserRoleRequest {
  @NotNull(message = "User id is required")
  private Long userId;
  @NotNull(message = "Role type is required")
  private RoleType roleType;
}
