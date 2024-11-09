package com.fastcampus.healthcare.entity;

import com.fastcampus.healthcare.common.constant.AppointmentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "appointment")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Appointment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "patient_id", nullable = false)
  private Long patientId;

  @Column(name = "doctor_id", nullable = false)
  private Long doctorId;

  @Column(name = "hospital_id", nullable = false)
  private Long hospitalId;

  @Column(name = "meeting_id")
  private String meetingId;

  @Column(name = "doctor_specialization_id", nullable = false)
  private Long doctorSpecializationId;

  @Column(name = "appointment_date", nullable = false)
  private LocalDate appointmentDate;

  @Column(name = "start_time", nullable = false)
  private LocalTime startTime;

  @Column(name = "end_time", nullable = false)
  private LocalTime endTime;

  @Column(name = "consultation_type", nullable = false, length = 10)
  private String consultationType;

  @Column(nullable = false, length = 20)
  @Enumerated(EnumType.STRING)
  private AppointmentStatus status;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;
}
