package com.fastcampus.healthcare.repository;

import com.fastcampus.healthcare.entity.Appointment;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
  @Query(value = "SELECT * FROM appointment " +
      "WHERE doctor_id = :doctorId " +
      "AND appointment_date = :date " +
      "AND consultation_type = :consultationType " +
      "AND status = 'SCHEDULED' " +
      "AND ((start_time < :endTime AND end_time > :startTime) " +
      "OR (start_time = :startTime AND end_time = :endTime)) " +
      "FOR UPDATE",
      nativeQuery = true)
  List<Appointment> findOverlappingAppointments(
      @Param("doctorId") Long doctorId,
      @Param("date") LocalDate date,
      @Param("startTime") LocalTime startTime,
      @Param("endTime") LocalTime endTime,
      @Param("consultationType") String consultationType
  );
  List<Appointment> findByPatientIdOrderByAppointmentDateDescStartTimeDesc(Long patientId);
  List<Appointment> findByDoctorIdAndAppointmentDateOrderByStartTimeAsc(Long doctorId, LocalDate appointmentDate);
  @Query(value = "SELECT * FROM appointment " +
      "WHERE doctor_id = :doctorId " +
      "AND appointment_date >= :startDate " +
      "AND appointment_date <= :endDate " +
      "ORDER BY appointment_date ASC, start_time ASC",
      nativeQuery = true)
  List<Appointment> findDoctorAppointmentsInDateRange(
      @Param("doctorId") Long doctorId,
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate
  );
}
