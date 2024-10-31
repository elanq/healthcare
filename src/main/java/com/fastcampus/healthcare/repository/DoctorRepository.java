package com.fastcampus.healthcare.repository;

import com.fastcampus.healthcare.entity.Doctor;
import java.util.List;
import java.util.Optional;
import javax.print.Doc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
  List<Doctor> findByHospitalId(Long hospitalId);
  Optional<Doctor> findByUserId(Long userId);
}
