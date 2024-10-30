package com.fastcampus.healthcare.service;

import com.fastcampus.healthcare.model.AuthRequest;
import com.fastcampus.healthcare.model.UserInfo;

public interface AuthService {
  UserInfo authenticate(AuthRequest  authRequest);
}
