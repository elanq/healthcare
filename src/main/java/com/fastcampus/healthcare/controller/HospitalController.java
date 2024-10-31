package com.fastcampus.healthcare.controller;

import com.fastcampus.healthcare.common.exception.ForbiddenAccessException;
import com.fastcampus.healthcare.model.HospitalResponse;
import com.fastcampus.healthcare.model.UserInfo;
import com.fastcampus.healthcare.model.UserResponse;
import com.fastcampus.healthcare.model.UserUpdateRequest;
import com.fastcampus.healthcare.service.HospitalService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import java.util.Objects;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/hospitals")
@SecurityRequirement(name = "Bearer")
public class HospitalController {

  private final HospitalService hospitalService;

  @GetMapping
  public ResponseEntity<Page<HospitalResponse>> search(
      @RequestParam(required = false) String keyword,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "name") String sortBy,
      @RequestParam(defaultValue = "asc") String sortDir
  ) {
    Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Direction.DESC : Direction.ASC;
    PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sortBy));

    Page<HospitalResponse> hospitalResponses = hospitalService.search(keyword, pageRequest);
    return ResponseEntity.ok(hospitalResponses);
  }

  @PutMapping("/{id}")
  public ResponseEntity<HospitalResponse> get(@PathVariable Long id
  ) {
    HospitalResponse response = hospitalService.get(id);
    return ResponseEntity.ok(response);
  }}
