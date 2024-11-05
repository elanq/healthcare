package com.fastcampus.healthcare.model;

import com.fastcampus.healthcare.common.constant.AppointmentStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonNaming(SnakeCaseStrategy.class)
public class PaymentNotification {
  private String id;
  private BigDecimal amount;
  private String status;
  private Instant created;
  private boolean isHigh;
  private Instant paidAt;
  private Instant updated;
  private String userId;
  private String currency;
  private String paymentId;
  private String description;
  private String externalId;
  private BigDecimal paidAmount;
  private String payerEmail;
  private String ewalletType;
  private String merchantName;
  private String paymentMethod;
  private String paymentChannel;
  private String paymentMethodId;
}