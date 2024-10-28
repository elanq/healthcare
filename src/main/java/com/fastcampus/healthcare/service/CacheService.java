package com.fastcampus.healthcare.service;

import com.fasterxml.jackson.core.type.TypeReference;
import java.time.Duration;
import java.util.Optional;

public interface CacheService {
  <T> Optional<T> get(String key, Class<T> clazz);
  <T> Optional<T> get(String key, TypeReference<T> clazz);
  <T> void put(String key, T value);
  <T> void put(String key, T value, Duration ttl);
  void evict(String key);
}
