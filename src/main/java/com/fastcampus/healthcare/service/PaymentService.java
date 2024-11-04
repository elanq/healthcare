package com.fastcampus.healthcare.service;

import com.fastcampus.healthcare.entity.Appointment;
import com.fastcampus.healthcare.model.PaymentResponse;

public interface PaymentService {
  PaymentResponse createPayment(Appointment appointment);
  PaymentResponse findByAppointmentId(Long appointmentId);
}
