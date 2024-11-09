package com.fastcampus.healthcare.service;

import com.fastcampus.healthcare.common.constant.AppointmentStatus;
import com.fastcampus.healthcare.common.constant.RoleType;
import com.fastcampus.healthcare.common.exception.AppointmentConflictException;
import com.fastcampus.healthcare.common.exception.ForbiddenAccessException;
import com.fastcampus.healthcare.common.exception.ResourceNotFoundException;
import com.fastcampus.healthcare.entity.Appointment;
import com.fastcampus.healthcare.entity.Doctor;
import com.fastcampus.healthcare.entity.DoctorSpecialization;
import com.fastcampus.healthcare.entity.Hospital;
import com.fastcampus.healthcare.entity.HospitalDoctorFee;
import com.fastcampus.healthcare.entity.User;
import com.fastcampus.healthcare.model.AppointmentMeetingResponse;
import com.fastcampus.healthcare.model.AppointmentRequest;
import com.fastcampus.healthcare.model.AppointmentRescheduleRequest;
import com.fastcampus.healthcare.model.AppointmentResponse;
import com.fastcampus.healthcare.model.DoctorResponse;
import com.fastcampus.healthcare.model.PaymentResponse;
import com.fastcampus.healthcare.model.UserResponse;
import com.fastcampus.healthcare.repository.AppointmentRepository;
import com.fastcampus.healthcare.repository.DoctorAvailabilityRepository;
import com.fastcampus.healthcare.repository.DoctorRepository;
import com.fastcampus.healthcare.repository.DoctorSpecializationRepository;
import com.fastcampus.healthcare.repository.HospitalDoctorFeeRepository;
import com.fastcampus.healthcare.repository.HospitalRepository;
import com.fastcampus.healthcare.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
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
  private final PaymentService paymentService;
  private final HospitalRepository hospitalRepository;
  private final UserService userService;
  private final DoctorService doctorService;

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

    PaymentResponse paymentResponse = paymentService.createPayment(appointment);

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
        .paymentDetail(paymentResponse)
        .build();
  }

  @Override
  @Transactional
  public AppointmentResponse rescheduleAppointment(Long userId, Long appointmentId,
      AppointmentRescheduleRequest request) {

    Appointment appointment = appointmentRepository.findByIdAndLock(appointmentId)
        .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

    if (!appointment.getPatientId().equals(userId)) {
      throw new ForbiddenAccessException("Can't reschedule other appointment");
    }

    if (appointment.getStatus() != AppointmentStatus.PENDING &&
        appointment.getStatus() != AppointmentStatus.SCHEDULED) {
      throw new IllegalStateException("Appointment cannot be rescheduled");
    }

    if (request.getAppointmentDate().isBefore(LocalDate.now())) {
      throw new IllegalArgumentException("Cannot reschedule to a past date");
    }

    boolean isDoctorAvailable = doctorAvailabilityRepository.isDoctorAvailable(
        appointment.getDoctorId(),
        request.getAppointmentDate(),
        request.getStartTime(),
        request.getEndTime(),
        appointment.getConsultationType()
    );

    if (!isDoctorAvailable) {
      throw new AppointmentConflictException("The doctor is not available for the requested time slot");
    }

    List<Appointment> overlappingAppointments = appointmentRepository.findOverlappingAppointments(
        appointment.getDoctorId(),
        request.getAppointmentDate(),
        request.getStartTime(),
        request.getEndTime(),
        appointment.getConsultationType()
    );

    if (!overlappingAppointments.isEmpty()) {
      throw new AppointmentConflictException("The selected time slot conflicts with existing appointments");
    }

    appointment.setAppointmentDate(request.getAppointmentDate());
    appointment.setStartTime(request.getStartTime());
    appointment.setEndTime(request.getEndTime());

    appointmentRepository.save(appointment);

    paymentService.recalculatePayment(appointment);

    return convertToAppointmentResponse(appointment);
  }

  @Override
  public List<AppointmentResponse> listUserAppointments(Long userId) {
    List<Appointment> appointments = appointmentRepository.findByPatientIdOrderByAppointmentDateDescStartTimeDesc(userId);
    return appointments.stream()
        .map(this::convertToAppointmentResponse)
        .toList();
  }

  @Override
  @Transactional
  public void cancelAppointment(Long userId, Long appointmentId) {
    Appointment appointment = appointmentRepository.findByIdAndLock(appointmentId)
        .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

    if  (!appointment.getPatientId().equals(userId)) {
      throw new ForbiddenAccessException("Can't reschedule other appointment");
    }

    if (appointment.getStatus() != AppointmentStatus.PENDING) {
      throw new IllegalStateException("Only PENDING appointments can be cancelled");
    }

    appointment.setStatus(AppointmentStatus.CANCELLED);
    appointmentRepository.save(appointment);

    paymentService.cancelPaymentForAppointment(appointmentId);
  }

  @Override
  public AppointmentResponse findById(Long appointmentId) {
    return appointmentRepository.findById(appointmentId)
        .map(this::convertToAppointmentResponse)
        .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
  }

  @Override
  public AppointmentMeetingResponse getMeetingStatus(Long userId, Long appointmentId) {
    UserResponse user = userService.getUserById(userId);
    AppointmentResponse appointmentResponse = findById(appointmentId);

    if (appointmentResponse.getStatus() != AppointmentStatus.SCHEDULED) {
      throw new ForbiddenAccessException("Appointment is not scheduled yet or already expired");
    }

    if (user.getRoles().contains(RoleType.DOCTOR)) {
      Doctor doctor =  doctorService.getDoctorByUserId(user.getUserId());
      if (!Objects.equals(doctor.getId(), appointmentResponse.getDoctorId())) {
        throw new ForbiddenAccessException("Doctor can't access this appointment");
      }
      return AppointmentMeetingResponse.builder()
          .doctorId(doctor.getId())
          .status(appointmentResponse.getStatus())
          .build();
    }

    if (!Objects.equals(user.getUserId(), appointmentResponse.getPatientId())) {
      throw new ForbiddenAccessException("User can't access this appointment");
    }

    LocalDate today = LocalDate.now();
    if (!today.equals(appointmentResponse.getAppointmentDate())) {
      throw new ForbiddenAccessException("Meeting is not yet started or has expired");
    }

    LocalTime now = LocalTime.now();
    if (now.isBefore(appointmentResponse.getStartTime()) || now.isAfter(appointmentResponse.getEndTime())) {
      throw new ForbiddenAccessException("Meeting is not yet started or has passed");
    }

    return AppointmentMeetingResponse.builder()
        .doctorId(appointmentResponse.getDoctorId())
        .patientId(appointmentResponse.getPatientId())
        .status(appointmentResponse.getStatus())
        .build();    }

  private AppointmentResponse convertToAppointmentResponse(Appointment appointment) {
    PaymentResponse paymentResponse = paymentService.findByAppointmentId(appointment.getId());
    return AppointmentResponse.builder()
        .id(appointment.getId())
        .patientId(appointment.getPatientId())
        .doctorId(appointment.getDoctorId())
        .appointmentDate(appointment.getAppointmentDate())
        .startTime(appointment.getStartTime())
        .endTime(appointment.getEndTime())
        .consultationType(appointment.getConsultationType())
        .status(appointment.getStatus())
        .paymentDetail(paymentResponse)
        .meetingId(appointment.getMeetingId())
        .build();
  }
}
