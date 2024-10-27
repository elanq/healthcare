package com.fastcampus.healthcare.controller;

import com.fastcampus.healthcare.common.exception.BadRequestException;
import com.fastcampus.healthcare.common.exception.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

  @GetMapping("/hello")
  public String hello() {
    return "Hello spring boot";
  }

  @GetMapping("/bad-request")
  public ResponseEntity<String> badRequestError() {
    throw new BadRequestException("ada error bad request");
  }

  @GetMapping("/generic-error")
  public ResponseEntity<String> genericError() {
    throw new RuntimeException("generic error");
  }

  @GetMapping("/not-found")
  public ResponseEntity<String> notFoundError() {
    throw new ResourceNotFoundException("data tidak ditemukan");
  }

}
