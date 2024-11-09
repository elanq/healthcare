package com.fastcampus.healthcare.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "videosdk")
public class VideoSDKProperties {
  private String apiKey;
  private String baseUrl = "https://api.videosdk.live/v2";
}
