package com.fastcampus.healthcare.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(Include.NON_NULL)
public class VideoSDKCreateRoomRequest {
  @JsonProperty("customRoomId")
  private String customRoomId;

  @JsonProperty("webhook")
  private String webhook;

  @JsonProperty("autoCloseConfig")
  private String autoCloseConfig;

  @JsonProperty("autoStartConfig")
  private String autoStartConfig;
}