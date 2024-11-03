package com.fastcampus.healthcare.repository;

import com.fastcampus.healthcare.entity.Doctor;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
  List<Doctor> findByHospitalId(Long hospitalId);
  Optional<Doctor> findByUserId(Long userId);
  @Query(value = "SELECT * FROM doctor WHERE " +
      "LOWER(name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
      "LOWER(bio) LIKE LOWER(CONCAT('%', :keyword, '%'))",
      nativeQuery = true)
  Page<Doctor> searchDoctors(@Param("keyword") String keyword, Pageable pageable);
}
