package com.fastcampus.healthcare.service;

import com.fastcampus.healthcare.model.AppointmentRequest;
import com.fastcampus.healthcare.model.AppointmentResponse;

public interface AppointmentService {
  AppointmentResponse bookAppointment(AppointmentRequest request);
}
