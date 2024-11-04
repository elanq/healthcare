package com.fastcampus.healthcare.repository;

import com.fastcampus.healthcare.entity.Doctor;
import com.fastcampus.healthcare.entity.DoctorAvailability;
import java.time.LocalDate;
import java.time.LocalTime;
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

  @Query(value = "SELECT * FROM doctor_availability " +
      "WHERE doctor_id = :doctorId " +
      "AND date BETWEEN :startDate AND :endDate " +
      "ORDER BY date ASC, start_time ASC",
      nativeQuery = true)
  List<DoctorAvailability> findAvailableSlotsByDoctorAndDateRange(
      @Param("doctorId") Long doctorId,
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate
  );

  @Query(value = "SELECT COUNT(*) > 0 FROM doctor_availability " +
      "WHERE doctor_id = :doctorId " +
      "AND date = :date " +
      "AND start_time <= :startTime " +
      "AND end_time >= :endTime " +
      "AND consultation_type = :consultationType " +
      "AND is_available = true",
      nativeQuery = true)
  boolean isDoctorAvailable(
      @Param("doctorId") Long doctorId,
      @Param("date") LocalDate date,
      @Param("startTime") LocalTime startTime,
      @Param("endTime") LocalTime endTime,
      @Param("consultationType") String consultationType
  );
}
