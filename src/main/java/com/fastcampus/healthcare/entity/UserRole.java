package com.fastcampus.healthcare.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_role")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRole {

  @EmbeddedId
  private UserRoleId id;

  @Embeddable
  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class UserRoleId implements Serializable {
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "role_id")
    private Long roleId;
  }
}
