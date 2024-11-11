package com.fastcampus.healthcare.service;

import com.fastcampus.healthcare.common.constant.AppointmentStatus;
import com.fastcampus.healthcare.config.VideoSDKClient;
import com.fastcampus.healthcare.entity.Appointment;
import com.fastcampus.healthcare.model.VideoSDKCreateRoomRequest;
import com.fastcampus.healthcare.model.VideoSDKCreateRoomResponse;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class MeetingServiceImpl implements
    MeetingService {

  private final VideoSDKClient videoSDKClient;

  @Override
  public void createMeetingRoom(Appointment appointment) {
    if (!appointment.getStatus().equals(AppointmentStatus.SCHEDULED)) {
      log.error("Appointment with id {} is not scheduled. actual {}", appointment.getId(), appointment.getStatus());
      return;
    }

    if (appointment.getMeetingId() != null && !appointment.getMeetingId().isEmpty()) {
      log.error("Appointment with id {} already has scheduled meeting id", appointment.getId());
      return;
    }

    VideoSDKCreateRoomRequest videoSDKCreateRoomRequest = VideoSDKCreateRoomRequest
        .builder()
        .build();
    VideoSDKCreateRoomResponse response = videoSDKClient.createRoom(videoSDKCreateRoomRequest);
    appointment.setMeetingId(response.getRoomId());

  }
}
