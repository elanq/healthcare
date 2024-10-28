package com.fastcampus.healthcare.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CacheServiceImpl implements CacheService {

  private final RedisTemplate<String, String> redisTemplate;
  private final ObjectMapper objectMapper;

  @Override
  public <T> Optional<T> get(String key, Class<T> clazz) {
    String value = redisTemplate.opsForValue().get(key);
    try {
      return Optional.ofNullable(objectMapper.readValue(value, clazz));
    } catch (JsonProcessingException |  IllegalArgumentException e)  {
      log.error("Error while parsing value. error message: {}", e.getMessage());
      return Optional.empty();
    }
  }

  @Override
  public <T> Optional<T> get(String key, TypeReference<T> clazz) {
    String value = redisTemplate.opsForValue().get(key);
    try {
      return Optional.ofNullable(objectMapper.readValue(value, clazz));
    } catch (JsonProcessingException |  IllegalArgumentException e)  {
      log.error("Error while parsing value. error message: {}", e.getMessage());
      return Optional.empty();
    }
  }

  @Override
  public <T> void put(String key, T value) {
    try {
      String jsonValue = objectMapper.writeValueAsString(value);
      redisTemplate.opsForValue().set(key, jsonValue);
    } catch (JsonProcessingException e) {
      log.error("Error while put value. error message: {}", e.getMessage());
      throw new RuntimeException(e);
    }
  }

  @Override
  public <T> void put(String key, T value, Duration ttl) {
    try {
      String jsonValue = objectMapper.writeValueAsString(value);
      redisTemplate.opsForValue().set(key, jsonValue, ttl);
    } catch (JsonProcessingException e) {
      log.error("Error while put value. error message: {}", e.getMessage());
      throw new RuntimeException(e);
    }
  }

  @Override
  public void evict(String key) {
    redisTemplate.delete(key);
  }
}
