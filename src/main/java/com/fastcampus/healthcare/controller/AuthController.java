package com.fastcampus.healthcare.controller;

import com.fastcampus.healthcare.model.UserRegisterRequest;
import com.fastcampus.healthcare.model.UserResponse;
import com.fastcampus.healthcare.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

  private final UserService userService;

  // POST /api/v1/auth/register
  public ResponseEntity<UserResponse> register(
      @Valid @RequestBody UserRegisterRequest request
  ) {
    UserResponse response = userService.registerUser(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(response);
  }
}
