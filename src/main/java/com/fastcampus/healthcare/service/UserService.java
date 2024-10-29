package com.fastcampus.healthcare.service;

import com.fastcampus.healthcare.model.UserRegisterRequest;
import com.fastcampus.healthcare.model.UserResponse;

public interface UserService {
  UserResponse registerUser(UserRegisterRequest userRegisterRequest);
  UserResponse getUserById(Long id);
  UserResponse getByUsername(String username);
  boolean existsByUsername(String username);
  boolean existsByEmail(String email);
}
