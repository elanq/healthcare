package com.fastcampus.healthcare.repository;

import com.fastcampus.healthcare.entity.Doctor;
import com.fastcampus.healthcare.entity.Specialization;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpecializationRepository extends JpaRepository<Specialization, Long> {
  Optional<Specialization> findByNameContainingIgnoreCase(String name);
}
