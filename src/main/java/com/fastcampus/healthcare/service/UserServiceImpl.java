package com.fastcampus.healthcare.service;

import com.fastcampus.healthcare.common.constant.RoleType;
import com.fastcampus.healthcare.common.exception.BadRequestException;
import com.fastcampus.healthcare.common.exception.EmailAlreadyExistsException;
import com.fastcampus.healthcare.common.exception.InvalidPasswordException;
import com.fastcampus.healthcare.common.exception.ResourceNotFoundException;
import com.fastcampus.healthcare.common.exception.RoleNotFoundException;
import com.fastcampus.healthcare.common.exception.UserNotFoundException;
import com.fastcampus.healthcare.common.exception.UsernameAlreadyExistsException;
import com.fastcampus.healthcare.entity.Role;
import com.fastcampus.healthcare.entity.User;
import com.fastcampus.healthcare.entity.UserRole;
import com.fastcampus.healthcare.entity.UserRole.UserRoleId;
import com.fastcampus.healthcare.model.UserRegisterRequest;
import com.fastcampus.healthcare.model.UserResponse;
import com.fastcampus.healthcare.model.UserUpdateRequest;
import com.fastcampus.healthcare.repository.RoleRepository;
import com.fastcampus.healthcare.repository.UserRepository;
import com.fastcampus.healthcare.repository.UserRoleRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
  private final String USER_CACHE_KEY = "cache:user:";
  private final String USER_ROLES_CACHE_KEY = "cache:user:roles:";
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final UserRoleRepository userRoleRepository;
  private final PasswordEncoder passwordEncoder;
  private final CacheService cacheService;

  @Override
  @Transactional
  public UserResponse registerUser(UserRegisterRequest userRegisterRequest) {
    if (existsByEmail(userRegisterRequest.getEmail())) {
      throw new EmailAlreadyExistsException("Email "+userRegisterRequest.getEmail()+" is already taken");
    }

    if (existsByUsername(userRegisterRequest.getUsername())) {
      throw new EmailAlreadyExistsException("Username "+userRegisterRequest.getUsername()+" is already taken");
    }

    if (!userRegisterRequest.getPassword().equals(userRegisterRequest.getPasswordConfirmation())) {
      throw new BadRequestException("Password is not matched");
    }

    User user = User.builder()
        .username(userRegisterRequest.getUsername())
        .email(userRegisterRequest.getEmail())
        .password(passwordEncoder.encode(userRegisterRequest.getPassword()))
        .enabled(true)
        .build();

    userRepository.save(user);

    Role role = roleRepository.findByName(RoleType.PATIENT)
        .orElseThrow(() -> new RoleNotFoundException("Role is not found"));

    UserRole userRole = UserRole.builder()
        .id(new UserRoleId(user.getUserId(), role.getRoleId()))
        .build();
    userRoleRepository.save(userRole);

    return UserResponse.fromUserAndRoles(user, List.of(role));
  }

  @Override
  public UserResponse getUserById(Long id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    List<Role> roles = roleRepository.findByUserId(id);

    return UserResponse.fromUserAndRoles(user, roles);
  }

  @Override
  public UserResponse getByUsername(String username) {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
    List<Role> roles = roleRepository.findByUserId(user.getUserId());

    return UserResponse.fromUserAndRoles(user, roles);     }

  @Override
  public boolean existsByUsername(String username) {
    return userRepository.existsByUsername(username);
  }

  @Override
  public boolean existsByEmail(String email) {
    return userRepository.existsByEmail(email);
  }

  @Override
  @Transactional
  public UserResponse updateUser(Long id, UserUpdateRequest userUpdateRequest) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException("User with id " + id + " is not found"));

    if (userUpdateRequest.getCurrentPassword() != null &&
        userUpdateRequest.getNewPassword() != null) {
      if (!passwordEncoder.matches(userUpdateRequest.getCurrentPassword(), user.getPassword())) {
        throw new InvalidPasswordException("Current password is incorrect");
      }

      user.setPassword(passwordEncoder.encode(userUpdateRequest.getNewPassword()));
    }

    if (userUpdateRequest.getEmail() != null && !userUpdateRequest.getEmail().equals(user.getEmail())) {
      if (existsByEmail(userUpdateRequest.getEmail())) {
        throw new EmailAlreadyExistsException("Email is already in use");
      }
      user.setEmail(userUpdateRequest.getEmail());
    }

    if (userUpdateRequest.getUsername() != null && !userUpdateRequest.getUsername().equals(user.getUsername())) {
      if (existsByUsername(userUpdateRequest.getUsername())) {
        throw new UsernameAlreadyExistsException("Username is already in use");
      }
      user.setUsername(userUpdateRequest.getUsername());
    }


    userRepository.save(user);
    List<Role> roles = roleRepository.findByUserId(id);

    String userCacheKey = USER_CACHE_KEY + user.getUsername();
    String rolesCacheKey = USER_ROLES_CACHE_KEY + user.getUsername();

    cacheService.evict(userCacheKey);
    cacheService.evict(rolesCacheKey);

    return UserResponse.fromUserAndRoles(user, roles);
  }

  @Transactional
  @Override
  public UserResponse grantUserRole(Long userId, RoleType roleType) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException("User with id "+userId + " is not found"));

    Role role = roleRepository.findByName(roleType)
        .orElseThrow(() -> new RoleNotFoundException("Role with name "+roleType + " is not found"));

    Optional<UserRole> existingRole = userRoleRepository.existsByUserIdAndRoleId(userId, role.getRoleId());
    if (existingRole.isPresent()) {
      throw new IllegalStateException("User " + userId + " already has role " + roleType);
    }

    UserRoleId userRoleId = new UserRoleId();
    userRoleId.setRoleId(role.getRoleId());
    userRoleId.setUserId(user.getUserId());

    UserRole userRole = UserRole.builder()
        .id(userRoleId)
        .build();

    userRoleRepository.save(userRole);

    List<Role> userRoles = roleRepository.findByUserId(userId);
    return UserResponse.fromUserAndRoles(user, userRoles);
  }
}
