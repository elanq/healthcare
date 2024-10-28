package com.fastcampus.healthcare.repository;

import com.fastcampus.healthcare.entity.UserRole;
import com.fastcampus.healthcare.entity.UserRole.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {

  void deleteByIdUserId(Long userId);
}
