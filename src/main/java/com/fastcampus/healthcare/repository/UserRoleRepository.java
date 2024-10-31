package com.fastcampus.healthcare.repository;

import com.fastcampus.healthcare.entity.UserRole;
import com.fastcampus.healthcare.entity.UserRole.UserRoleId;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {

  void deleteByIdUserId(Long userId);

  @Query(value = """
      SELECT * FROM user_role
      WHERE user_id = :userId
      AND role_id = :roleId
      LIMIT 1
      """, nativeQuery = true)
  Optional<UserRole> existsByUserIdAndRoleId(Long userId, Long roleId);
}
