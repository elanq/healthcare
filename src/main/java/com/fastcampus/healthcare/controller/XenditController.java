package com.fastcampus.healthcare.controller;

import com.fastcampus.healthcare.model.PaymentNotification;
import com.fastcampus.healthcare.service.XenditService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/webhook/xendit")
public class XenditController {
  private final XenditService xenditService;

  @PostMapping("")
  public ResponseEntity<String> handleXenditWebhook(@RequestBody PaymentNotification payload) {
    try {
      xenditService.handlePaymentNotification(payload);
      return ResponseEntity.ok("Webhook processed successfully");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Error processing webhook");
    }
  }
}
