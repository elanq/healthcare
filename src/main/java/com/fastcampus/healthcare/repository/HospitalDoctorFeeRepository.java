package com.fastcampus.healthcare.repository;

import com.fastcampus.healthcare.entity.Hospital;
import com.fastcampus.healthcare.entity.HospitalDoctorFee;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HospitalDoctorFeeRepository extends JpaRepository<HospitalDoctorFee, Long> {
  Optional<HospitalDoctorFee > findByHospitalIdAndDoctorSpecializationId(Long hospitalId, Long doctorSpecId);
}
