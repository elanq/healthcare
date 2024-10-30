package com.fastcampus.healthcare.config;

import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import javax.crypto.SecretKey;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class JwtSecretConfig {
  @Value("${app.jwtSecret}")
  private String jwtSecret;
  @Value("${app.jwtExpirationTime}")
  private Duration jwtExpirationTime;

  @Bean
  public SecretKey signKey() {
    return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
  }
}
