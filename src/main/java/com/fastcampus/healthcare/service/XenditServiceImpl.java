package com.fastcampus.healthcare.service;

import com.fastcampus.healthcare.common.exception.PaymentException;
import com.fastcampus.healthcare.common.exception.ResourceNotFoundException;
import com.fastcampus.healthcare.entity.Appointment;
import com.fastcampus.healthcare.entity.Payment;
import com.fastcampus.healthcare.entity.User;
import com.fastcampus.healthcare.model.PaymentNotification;
import com.fastcampus.healthcare.model.PaymentResponse;
import com.fastcampus.healthcare.repository.AppointmentRepository;
import com.fastcampus.healthcare.repository.PaymentRepository;
import com.fastcampus.healthcare.repository.UserRepository;
import com.xendit.exception.XenditException;
import com.xendit.model.Invoice;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class XenditServiceImpl implements XenditService {

  private final AppointmentRepository appointmentRepository;
  private final UserRepository userRepository;
  private final PaymentRepository paymentRepository;

  @Override
  @Transactional
  public PaymentResponse createPayment(Payment payment) {

    Appointment appointment = appointmentRepository.findById(payment.getAppointmentId())
        .orElseThrow(() -> new ResourceNotFoundException(
            "Appointment not found for payment id " + payment.getTransactionId()));

    User user = userRepository.findById(appointment.getPatientId())
        .orElseThrow(() -> new ResourceNotFoundException(
            "User not found for payment id " + payment.getTransactionId()));

    Map<String, Object> params = new HashMap<>();
    params.put("external_id", payment.getTransactionId());
    params.put("amount", payment.getAmount().doubleValue());
    params.put("payer_email", user.getEmail()); // Assuming you have this in Order
    params.put("description", "Payment for Order #" + payment.getTransactionId());

    Invoice invoice = null;
    try {
      invoice = Invoice.create(params);
    } catch (XenditException e) {
      throw new PaymentException(e.getMessage());
    }

    payment.setXenditPaymentStatus(invoice.getStatus());
    payment.setXenditInvoiceId(invoice.getId());

    paymentRepository.save(payment);

    PaymentResponse paymentResponse = PaymentResponse.fromEntity(payment);
    paymentResponse.setPaymentUrl(invoice.getInvoiceUrl());
    return paymentResponse;
  }

  @Override
  public void handlePaymentNotification(PaymentNotification payload) {

  }
}
