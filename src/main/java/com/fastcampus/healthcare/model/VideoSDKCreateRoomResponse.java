package com.fastcampus.healthcare.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import lombok.Data;

@Data
public class VideoSDKCreateRoomResponse {
  @JsonProperty("roomId")
  private String roomId;

  @JsonProperty("customRoomId")
  private String customRoomId;

  @JsonProperty("userId")
  private String userId;

  @JsonProperty("disabled")
  private boolean disabled;

  @JsonProperty("createdAt")
  private Instant createdAt;

  @JsonProperty("updatedAt")
  private Instant updatedAt;

  @JsonProperty("id")
  private String id;

  @JsonProperty("links")
  private Links links;

  @Data
  public static class Links {
    @JsonProperty("get_room")
    private String getRoom;

    @JsonProperty("get_session")
    private String getSession;
  }
}