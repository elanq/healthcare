package com.fastcampus.healthcare.config;

import com.fastcampus.healthcare.model.VideoSDKCreateRoomRequest;
import com.fastcampus.healthcare.model.VideoSDKCreateRoomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class VideoSDKClient {
  private final VideoSDKProperties properties;
  private final RestTemplate restTemplate;

  public VideoSDKCreateRoomResponse createRoom(VideoSDKCreateRoomRequest request) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Authorization", properties.getApiKey());

    HttpEntity<VideoSDKCreateRoomRequest> entity = new HttpEntity<>(request, headers);

    return restTemplate.postForObject(
        properties.getBaseUrl() + "/rooms",
        entity,
        VideoSDKCreateRoomResponse.class
    );
  }
}