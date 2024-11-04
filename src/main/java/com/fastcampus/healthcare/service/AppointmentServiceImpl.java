package com.fastcampus.healthcare.service;

import com.fastcampus.healthcare.common.constant.AppointmentStatus;
import com.fastcampus.healthcare.common.exception.AppointmentConflictException;
import com.fastcampus.healthcare.common.exception.ResourceNotFoundException;
import com.fastcampus.healthcare.entity.Appointment;
import com.fastcampus.healthcare.entity.Doctor;
import com.fastcampus.healthcare.entity.DoctorSpecialization;
import com.fastcampus.healthcare.entity.Hospital;
import com.fastcampus.healthcare.entity.HospitalDoctorFee;
import com.fastcampus.healthcare.entity.User;
import com.fastcampus.healthcare.model.AppointmentRequest;
import com.fastcampus.healthcare.model.AppointmentResponse;
import com.fastcampus.healthcare.repository.AppointmentRepository;
import com.fastcampus.healthcare.repository.DoctorAvailabilityRepository;
import com.fastcampus.healthcare.repository.DoctorRepository;
import com.fastcampus.healthcare.repository.DoctorSpecializationRepository;
import com.fastcampus.healthcare.repository.HospitalDoctorFeeRepository;
import com.fastcampus.healthcare.repository.HospitalRepository;
import com.fastcampus.healthcare.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentServiceImpl implements
    AppointmentService {

  private final UserRepository userRepository;
  private final DoctorRepository doctorRepository;
  private final DoctorSpecializationRepository doctorSpecializationRepository;
  private final HospitalDoctorFeeRepository hospitalDoctorFeeRepository;
  private final DoctorAvailabilityRepository doctorAvailabilityRepository;
  private final AppointmentRepository appointmentRepository;
  private final HospitalRepository hospitalRepository;

  @Override
  @Transactional
  public AppointmentResponse bookAppointment(AppointmentRequest request) {
    User user = userRepository.findById(request.getUserId())
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    Doctor doctor = doctorRepository.findById(request.getDoctorId())
        .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

    // Fetch hospital name
    Hospital hospital = hospitalRepository.findById(doctor.getHospitalId())
        .orElseThrow(() -> new ResourceNotFoundException("Hospital not found"));

    DoctorSpecialization doctorSpecialization = doctorSpecializationRepository.findById(request.getDoctorSpecializationId())
        .orElseThrow(() -> new ResourceNotFoundException("Doctor specialization not found"));

    HospitalDoctorFee fee = hospitalDoctorFeeRepository
        .findByHospitalIdAndDoctorSpecializationId(doctor.getHospitalId(), doctorSpecialization.getId())
        .orElseThrow(() -> new ResourceNotFoundException("Doctor specialization fee not found"));

    // Check if the doctor is available for the requested time slot
    boolean isDoctorAvailable = doctorAvailabilityRepository.isDoctorAvailable(
        request.getDoctorId(),
        request.getAppointmentDate(),
        request.getStartTime(),
        request.getEndTime(),
        fee.getConsultationType()
    );

    if (!isDoctorAvailable) {
      throw new AppointmentConflictException("The doctor is not available for the requested time slot");
    }

    // Check for overlapping appointments with pessimistic lock
    List<Appointment> overlappingAppointments = appointmentRepository.findOverlappingAppointments(
        request.getDoctorId(),
        request.getAppointmentDate(),
        request.getStartTime(),
        request.getEndTime(),
        fee.getConsultationType()
    );
    if (!overlappingAppointments.isEmpty()) {
      throw new AppointmentConflictException("The selected time slot overlaps with existing scheduled appointments of the same consultation type");
    }

    // Create the appointment
    Appointment appointment = Appointment.builder()
        .patientId(request.getUserId())
        .doctorId(request.getDoctorId())
        .hospitalId(doctor.getHospitalId())
        .doctorSpecializationId(request.getDoctorSpecializationId())
        .appointmentDate(request.getAppointmentDate())
        .startTime(request.getStartTime())
        .endTime(request.getEndTime())
        .consultationType(fee.getConsultationType())
        .status(AppointmentStatus.PENDING)
        .build();

    // Save the appointment
    appointmentRepository.save(appointment);

// Create and return AppointmentResponse
    return AppointmentResponse.builder()
        .id(appointment.getId())
        .patientId(user.getUserId())
        .patientName(user.getUsername())
        .doctorId(doctor.getId())
        .doctorName(doctor.getName())
        .hospitalId(hospital.getId())
        .hospitalName(hospital.getName())
        .appointmentDate(appointment.getAppointmentDate())
        .startTime(appointment.getStartTime())
        .endTime(appointment.getEndTime())
        .consultationType(appointment.getConsultationType())
        .status(appointment.getStatus())
        .build();
  }
}
