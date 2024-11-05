package com.fastcampus.healthcare.service;

import com.fastcampus.healthcare.entity.Payment;
import com.fastcampus.healthcare.model.PaymentNotification;
import com.fastcampus.healthcare.model.PaymentResponse;

public interface XenditService {
  PaymentResponse createPayment(Payment payment);

  void handlePaymentNotification(PaymentNotification payload) ;
}
