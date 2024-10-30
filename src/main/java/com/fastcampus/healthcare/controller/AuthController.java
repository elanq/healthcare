package com.fastcampus.healthcare.controller;

import com.fastcampus.healthcare.model.AuthRequest;
import com.fastcampus.healthcare.model.AuthResponse;
import com.fastcampus.healthcare.model.UserInfo;
import com.fastcampus.healthcare.model.UserRegisterRequest;
import com.fastcampus.healthcare.model.UserResponse;
import com.fastcampus.healthcare.service.AuthService;
import com.fastcampus.healthcare.service.JwtService;
import com.fastcampus.healthcare.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

  private final UserService userService;
  private final AuthService authService;
  private final JwtService jwtService;

  // POST /api/v1/auth/register
  @PostMapping("/register")
  public ResponseEntity<UserResponse> register(
      @Valid @RequestBody UserRegisterRequest request
  ) {
    UserResponse response = userService.registerUser(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(response);
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> authenticate(
      @RequestBody AuthRequest authRequest
  ) {
    UserInfo userInfo = authService.authenticate(authRequest);
    String token = jwtService.generateToken(userInfo);
    AuthResponse authResponse = AuthResponse.fromUserInfo(userInfo, token);

    return ResponseEntity.ok(authResponse);
  }
}
