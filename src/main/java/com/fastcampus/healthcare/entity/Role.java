package com.fastcampus.healthcare.entity;

import com.fastcampus.healthcare.common.constant.RoleType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "roles")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Role {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "role_id")
  private Long roleId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, unique = true)
  private RoleType name;

  @Column(columnDefinition = "TEXT")
  private String description;
}
