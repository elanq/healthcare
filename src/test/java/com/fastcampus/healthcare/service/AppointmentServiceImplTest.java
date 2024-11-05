package com.fastcampus.healthcare.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fastcampus.healthcare.common.constant.AppointmentStatus;
import com.fastcampus.healthcare.common.constant.PaymentStatus;
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
import com.fastcampus.healthcare.model.PaymentResponse;
import com.fastcampus.healthcare.repository.AppointmentRepository;
import com.fastcampus.healthcare.repository.DoctorAvailabilityRepository;
import com.fastcampus.healthcare.repository.DoctorRepository;
import com.fastcampus.healthcare.repository.DoctorSpecializationRepository;
import com.fastcampus.healthcare.repository.HospitalDoctorFeeRepository;
import com.fastcampus.healthcare.repository.HospitalRepository;
import com.fastcampus.healthcare.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceImplTest {
  @Mock
  private UserRepository userRepository;
  @Mock
  private DoctorRepository doctorRepository;
  @Mock
  private DoctorSpecializationRepository doctorSpecializationRepository;
  @Mock
  private HospitalDoctorFeeRepository hospitalDoctorFeeRepository;
  @Mock
  private DoctorAvailabilityRepository doctorAvailabilityRepository;
  @Mock
  private AppointmentRepository appointmentRepository;
  @Mock
  private HospitalRepository hospitalRepository;
  @Mock
  private PaymentService paymentService;

  @InjectMocks
  private AppointmentServiceImpl appointmentService;

  private AppointmentRequest request;
  private User user;
  private Doctor doctor;
  private DoctorSpecialization doctorSpecialization;
  private HospitalDoctorFee fee;
  private Hospital hospital;
  private Appointment appointment;
  private PaymentResponse paymentResponse;

  @BeforeEach
  void setUp() {
    request = new AppointmentRequest();
    request.setUserId(1L);
    request.setDoctorId(2L);
    request.setDoctorSpecializationId(3L);
    request.setAppointmentDate(LocalDate.now().plusDays(1));
    request.setStartTime(LocalTime.of(10, 0));
    request.setEndTime(LocalTime.of(11, 0));

    user = new User();
    user.setUserId(1L);
    user.setUsername("patient");

    doctor = new Doctor();
    doctor.setId(2L);
    doctor.setName("Dr. Smith");
    doctor.setHospitalId(4L);

    doctorSpecialization = new DoctorSpecialization();
    doctorSpecialization.setId(3L);

    fee = new HospitalDoctorFee();
    fee.setConsultationType("ONLINE");

    hospital = new Hospital();
    hospital.setId(4L);
    hospital.setName("General Hospital");

    appointment = new Appointment();
    appointment.setId(5L);
    appointment.setStatus(AppointmentStatus.PENDING);

    paymentResponse = PaymentResponse.builder()
        .id(6L)
        .amount(BigDecimal.valueOf(100))
        .status(PaymentStatus.PENDING)
        .build();
  }

  @Test
  void bookAppointment_Success() {
    when(userRepository.findById(request.getUserId())).thenReturn(Optional.of(user));
    when(doctorRepository.findById(request.getDoctorId())).thenReturn(Optional.of(doctor));
    when(doctorSpecializationRepository.findById(request.getDoctorSpecializationId())).thenReturn(Optional.of(doctorSpecialization));
    when(hospitalDoctorFeeRepository.findByHospitalIdAndDoctorSpecializationId(doctor.getHospitalId(), doctorSpecialization.getId())).thenReturn(
        Optional.of(fee));
    when(doctorAvailabilityRepository.isDoctorAvailable(any(), any(), any(), any(), any())).thenReturn(true);
    when(appointmentRepository.findOverlappingAppointments(any(), any(), any(), any(), any())).thenReturn(
        Collections.emptyList());
    when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);
    when(hospitalRepository.findById(doctor.getHospitalId())).thenReturn(Optional.of(hospital));
    when(paymentService.createPayment(any(Appointment.class))).thenReturn(paymentResponse);

    AppointmentResponse response = appointmentService.bookAppointment(request);

    assertNotNull(response);
    assertEquals(user.getUserId(), response.getPatientId());
    assertEquals(doctor.getId(), response.getDoctorId());
    assertEquals(hospital.getId(), response.getHospitalId());
    assertEquals(appointment.getStatus(), response.getStatus());
    assertEquals(paymentResponse, response.getPaymentDetail());

    verify(appointmentRepository).save(any(Appointment.class));
    verify(paymentService).createPayment(any(Appointment.class));
  }

  @Test
  void bookAppointment_UserNotFound() {
    when(userRepository.findById(request.getUserId())).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> appointmentService.bookAppointment(request));
  }

  @Test
  void bookAppointment_DoctorNotAvailable() {
    when(userRepository.findById(request.getUserId())).thenReturn(Optional.of(user));
    when(doctorRepository.findById(request.getDoctorId())).thenReturn(Optional.of(doctor));
    when(doctorSpecializationRepository.findById(request.getDoctorSpecializationId())).thenReturn(Optional.of(doctorSpecialization));
    when(hospitalRepository.findById(doctor.getHospitalId())).thenReturn(Optional.of(hospital));
    when(hospitalDoctorFeeRepository.findByHospitalIdAndDoctorSpecializationId(doctor.getHospitalId(), doctorSpecialization.getId())).thenReturn(Optional.of(fee));
    when(doctorAvailabilityRepository.isDoctorAvailable(any(), any(), any(), any(), any())).thenReturn(false);

    assertThrows(AppointmentConflictException.class, () -> appointmentService.bookAppointment(request));
  }

  @Test
  void bookAppointment_OverlappingAppointments() {
    when(userRepository.findById(request.getUserId())).thenReturn(Optional.of(user));
    when(doctorRepository.findById(request.getDoctorId())).thenReturn(Optional.of(doctor));
    when(doctorSpecializationRepository.findById(request.getDoctorSpecializationId())).thenReturn(Optional.of(doctorSpecialization));
    when(hospitalRepository.findById(doctor.getHospitalId())).thenReturn(Optional.of(hospital));
    when(hospitalDoctorFeeRepository.findByHospitalIdAndDoctorSpecializationId(doctor.getHospitalId(), doctorSpecialization.getId())).thenReturn(Optional.of(fee));
    when(doctorAvailabilityRepository.isDoctorAvailable(any(), any(), any(), any(), any())).thenReturn(true);
    when(appointmentRepository.findOverlappingAppointments(any(), any(), any(), any(), any())).thenReturn(Collections.singletonList(new Appointment()));

    assertThrows(AppointmentConflictException.class, () -> appointmentService.bookAppointment(request));
  }

}