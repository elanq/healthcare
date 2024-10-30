package com.fastcampus.healthcare.controller;

import com.fastcampus.healthcare.common.exception.ForbiddenAccessException;
import com.fastcampus.healthcare.model.UserInfo;
import com.fastcampus.healthcare.model.UserResponse;
import com.fastcampus.healthcare.model.UserUpdateRequest;
import com.fastcampus.healthcare.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
@SecurityRequirement(name = "Bearer")
public class UserController {

  private final UserService userService;

  @GetMapping("/me")
  public ResponseEntity<UserResponse> me() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    UserInfo userInfo = (UserInfo) authentication.getPrincipal();

    UserResponse userResponse = UserResponse.fromUserAndRoles(userInfo.getUser(), userInfo.getRoles());
    return ResponseEntity.ok(userResponse);
  }

  @PutMapping("/{id}")
  public ResponseEntity<UserResponse> updateUser(@PathVariable Long id,
      @Valid @RequestBody UserUpdateRequest updateRequest
      ) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    UserInfo userInfo = (UserInfo) authentication.getPrincipal();

    if (!Objects.equals(userInfo.getUser().getUserId(), id)) {
      throw new ForbiddenAccessException("User is not allowed to update");
    }

    UserResponse updatedUser = userService.updateUser(id, updateRequest);
    return ResponseEntity.ok(updatedUser);
  }
}
