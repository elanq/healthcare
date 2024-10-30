package com.fastcampus.healthcare.service;

import com.fastcampus.healthcare.common.exception.InvalidPasswordException;
import com.fastcampus.healthcare.model.AuthRequest;
import com.fastcampus.healthcare.model.UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

  private final AuthenticationManager authenticationManager;

  @Override
  public UserInfo authenticate(AuthRequest authRequest) {

    try {
      Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()
          ));

      UserInfo userInfo = (UserInfo) authentication.getPrincipal();
      return userInfo;
    } catch (Exception ex) {
      throw new InvalidPasswordException("Invalid username or password");
    }
  }
}
