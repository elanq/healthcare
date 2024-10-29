package com.fastcampus.healthcare.service;

import com.fastcampus.healthcare.model.UserInfo;

public interface JwtService {
  String generateToken(UserInfo userInfo);
  boolean validateToken(String token);
  String getUsernameFromToken(String token);
}
