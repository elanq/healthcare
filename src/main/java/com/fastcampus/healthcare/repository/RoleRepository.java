package com.fastcampus.healthcare.repository;

import com.fastcampus.healthcare.common.constant.RoleType;
import com.fastcampus.healthcare.entity.Role;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
  Optional<Role> findByName(RoleType name);

  @Query(value = """
      SELECT r.* FROM roles r
      JOIN user_role ur ON ur.role_id = r.role_id
      JOIN users u ON ur.user_id = u.user_id     
      WHERE u.user_id = :userId
      """, nativeQuery = true)
  List<Role> findByUserId(Long userId);
}
