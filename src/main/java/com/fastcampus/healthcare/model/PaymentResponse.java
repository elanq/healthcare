package com.fastcampus.healthcare.model;

import com.fastcampus.healthcare.common.constant.PaymentStatus;
import com.fastcampus.healthcare.entity.Payment;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(SnakeCaseStrategy.class)
public class PaymentResponse {
  private Long id;
  private Long appointmentId;
  private BigDecimal amount;
  private String paymentMethod;
  private String transactionId;
  private PaymentStatus status;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public static PaymentResponse fromEntity(Payment payment) {
    return PaymentResponse.builder()
        .id(payment.getId())
        .appointmentId(payment.getAppointmentId())
        .amount(payment.getAmount())
        .paymentMethod(payment.getPaymentMethod())
        .transactionId(payment.getTransactionId())
        .status(payment.getStatus())
        .createdAt(payment.getCreatedAt())
        .updatedAt(payment.getUpdatedAt())
        .build();
  }
}
