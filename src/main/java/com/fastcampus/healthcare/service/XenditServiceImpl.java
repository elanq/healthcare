package com.fastcampus.healthcare.service;

import com.fastcampus.healthcare.common.constant.AppointmentStatus;
import com.fastcampus.healthcare.common.constant.PaymentStatus;
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
import java.util.Objects;
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
  private final MeetingService meetingService;

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
  @Transactional
  public void handlePaymentNotification(PaymentNotification payload) {
    String invoiceId = payload.getId();
    String status = payload.getStatus();

    // Fetch the order
    Payment payment = paymentRepository.findByXenditInvoiceId(invoiceId)
        .orElseThrow(
            () -> new ResourceNotFoundException("Order not found for invoice ID: " + invoiceId));

    // Update order status
    payment.setXenditPaymentStatus(status);
    switch (status) {
      case "PAID":
        handleOnSuccess(payment);
        break;
      case "EXPIRED":
        handleOnCancellation(payment);
        break;
      case "FAILED":
        payment.setStatus(PaymentStatus.FAILED);
        break;
      case "PENDING":
        payment.setStatus(PaymentStatus.PENDING);
        break;
      default:
    }

    // Update payment method if available
    if (payload.getPaymentMethod() != null) {
      payment.setPaymentMethod(payload.getPaymentMethod());
    }
    // Save the updated order
    paymentRepository.save(payment);
  }

  private void handleOnCancellation(Payment payment) {
    payment.setStatus(PaymentStatus.CANCELLED);
    Appointment appointment = appointmentRepository.findById(payment.getAppointmentId())
        .orElseThrow(
            () -> new ResourceNotFoundException("Appointment not found for transaction ID: " + payment.getTransactionId()));
    appointment.setStatus(AppointmentStatus.CANCELLED);

    try {
      Invoice.expire(payment.getXenditInvoiceId());
    } catch (XenditException e) {
      throw new PaymentException(e.getMessage());
    }
    appointmentRepository.save(appointment);
  }

  private void handleOnSuccess(Payment payment) {
    payment.setStatus(PaymentStatus.COMPLETED);

    Appointment appointment = appointmentRepository.findById(payment.getAppointmentId())
        .orElseThrow(
            () -> new ResourceNotFoundException("Appointment not found for transaction ID: " + payment.getTransactionId()));
    appointment.setStatus(AppointmentStatus.SCHEDULED);
    appointmentRepository.save(appointment);

    if (Objects.equals(appointment.getConsultationType(), "ONLINE"))  {
      meetingService.createMeetingRoom(appointment);
    }
  }

}
