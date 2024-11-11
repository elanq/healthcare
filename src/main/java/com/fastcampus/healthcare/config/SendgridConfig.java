package com.fastcampus.healthcare.config;

import com.sendgrid.SendGrid;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class SendgridConfig {
  @Value("${sendgrid.api-key}")
  private String sendgridApiKey;

  @Value("${sendgrid.from-email}")
  private String fromEmail;

  @Value("${email.template.payment-successful}")
  private String paymentSuccessTemplateId;

  @Value("${email.template.meeting-created}")
  private String meetingCreatedTemplateId;

  @Bean
  public SendGrid sendGridConfig() {
    return new SendGrid(sendgridApiKey);
  }
}
