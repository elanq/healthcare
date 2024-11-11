package com.fastcampus.healthcare.service;

import com.fastcampus.healthcare.entity.Appointment;

public interface EmailService {

  void notifySuccessfulPayment(Appointment appointment);
  void notifyMeetingCreated(Appointment appointment);
}
