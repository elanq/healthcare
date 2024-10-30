package com.fastcampus.healthcare.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.fastcampus.healthcare.common.constant.RoleType;
import com.fastcampus.healthcare.common.exception.InvalidPasswordException;
import com.fastcampus.healthcare.entity.Role;
import com.fastcampus.healthcare.entity.User;
import com.fastcampus.healthcare.model.AuthRequest;
import com.fastcampus.healthcare.model.UserInfo;
import java.util.List;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

  @Mock
  private AuthenticationManager authenticationManager;

  @InjectMocks
  private AuthServiceImpl authService;

  private AuthRequest authRequest;
  private UserInfo userInfo;

  @BeforeEach
  void setup() {
    authRequest = new AuthRequest("testUser", "testPassword");
    userInfo = new UserInfo(
        User.builder()
            .username("testUser")
            .build(),
        List.of(Role.builder()
                .name(RoleType.PATIENT)
            .build())
    );
  }

  @Test
  void authenticate_SuccessfulAuthentication_ReturnUserInfo() {
    Authentication  authentication = new UsernamePasswordAuthenticationToken(userInfo, null);

    when(authenticationManager.authenticate(any())).thenReturn(authentication);

    UserInfo result = authService.authenticate(authRequest);

    assertNotNull(result);
    assertEquals(userInfo.getUsername(), result.getUsername());
  }

  @Test
  void authenticate_FailedAuthentication_ThrowsInvalidPasswordException() {
    // Arrange
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenThrow(new RuntimeException("Authentication failed"));
    // Act & Assert
    assertThrows(InvalidPasswordException.class, () -> authService.authenticate(authRequest));
  }
}