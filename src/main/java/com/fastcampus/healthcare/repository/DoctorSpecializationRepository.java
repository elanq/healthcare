package com.fastcampus.healthcare.repository;

import com.fastcampus.healthcare.entity.Doctor;
import com.fastcampus.healthcare.entity.DoctorSpecialization;
import com.fastcampus.healthcare.entity.Specialization;
import java.util.List;
import java.util.Optional;
import javax.print.Doc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoctorSpecializationRepository extends JpaRepository<DoctorSpecialization, Long> {
  List<DoctorSpecialization> findByDoctorId(Long doctorId);
  List<DoctorSpecialization> findBySpecializationId(Long specializationId);
  Optional<DoctorSpecialization> findByDoctorIdAndSpecializationId(Long doctorId, Long specializationId);
}
