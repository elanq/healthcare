package com.fastcampus.healthcare.service;

import com.fastcampus.healthcare.common.constant.AppointmentStatus;
import com.fastcampus.healthcare.common.constant.PaymentStatus;
import com.fastcampus.healthcare.entity.Appointment;
import com.fastcampus.healthcare.entity.DoctorSpecialization;
import com.fastcampus.healthcare.entity.Payment;
import com.fastcampus.healthcare.model.PaymentResponse;
import com.fastcampus.healthcare.repository.DoctorSpecializationRepository;
import com.fastcampus.healthcare.repository.PaymentRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Service
public class PaymentServiceImpl implements
    PaymentService {

  private final PaymentRepository paymentRepository;
  private final DoctorSpecializationRepository doctorSpecializationRepository;

  @Override
  @Transactional
  public PaymentResponse createPayment(Appointment appointment) {
    if (appointment.getStatus() != AppointmentStatus.PENDING) {
      throw new IllegalStateException("Payment can only be created for appointments with PENDING status");
    }

    DoctorSpecialization doctorSpecialization = doctorSpecializationRepository
        .findById(appointment.getDoctorSpecializationId())
        .orElseThrow(() -> new IllegalStateException("Doctor specialization not found"));

    BigDecimal hourlyFee = doctorSpecialization.getBaseFee();
    BigDecimal amount = calculateAmount(appointment, hourlyFee);
    String transactionId = UUID.randomUUID().toString();

    Payment payment = Payment.builder()
        .appointmentId(appointment.getId())
        .amount(amount)
        .paymentMethod("NOT_SELECTED") // This can be updated later when the user selects a payment method
        .status(PaymentStatus.PENDING)
        .transactionId(transactionId)
        .build();

    paymentRepository.save(payment);
    return PaymentResponse.fromEntity(payment);
  }

  @Override
  public PaymentResponse findByAppointmentId(Long appointmentId) {
    return paymentRepository.findByAppointmentId(appointmentId)
        .map(PaymentResponse::fromEntity)
        .orElse(null);
  }

  private BigDecimal calculateAmount(Appointment appointment, BigDecimal hourlyFee) {
    Duration duration = Duration.between(appointment.getStartTime(), appointment.getEndTime());
    long hours = duration.toHours();
    if (duration.toMinutesPart() > 0 || duration.toSecondsPart() > 0) {
      hours += 1; // Round up to the next hour
    }
    return hourlyFee.multiply(BigDecimal.valueOf(hours)).setScale(2, RoundingMode.HALF_UP);
  }
}
