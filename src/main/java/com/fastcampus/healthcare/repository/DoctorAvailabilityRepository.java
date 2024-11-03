package com.fastcampus.healthcare.repository;

import com.fastcampus.healthcare.entity.Doctor;
import com.fastcampus.healthcare.entity.DoctorAvailability;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DoctorAvailabilityRepository extends JpaRepository<DoctorAvailability, Long> {
  @Query(value = "SELECT * FROM doctor_availability " +
      "WHERE doctor_id = :doctorId " +
      "AND date >= CURRENT_DATE " +
      "ORDER BY date ASC, start_time ASC",
      nativeQuery = true)
  List<DoctorAvailability> findAvailabilitiesByDoctorIdFromToday(@Param("doctorId") Long doctorId);
}
