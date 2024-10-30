package com.fastcampus.healthcare.repository;

import com.fastcampus.healthcare.entity.Hospital;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Long> {
  Page<Hospital> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
