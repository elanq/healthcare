package com.fastcampus.healthcare.service;

import com.fastcampus.healthcare.model.AppointmentMeetingResponse;
import com.fastcampus.healthcare.model.AppointmentRequest;
import com.fastcampus.healthcare.model.AppointmentRescheduleRequest;
import com.fastcampus.healthcare.model.AppointmentResponse;
import java.util.List;

public interface AppointmentService {
  AppointmentResponse bookAppointment(AppointmentRequest request);

  AppointmentResponse rescheduleAppointment(Long userId, Long appointmentId, AppointmentRescheduleRequest request);
  List<AppointmentResponse> listUserAppointments(Long userId);
  void cancelAppointment(Long userId, Long appointmentId);
  AppointmentResponse findById(Long appointmentId);
//  List<AppointmentResponse> listDoctorAppointments(Long doctorId);
  AppointmentMeetingResponse getMeetingStatus(Long userId, Long appointmentId);
}
