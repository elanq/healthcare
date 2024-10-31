package com.fastcampus.healthcare.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fastcampus.healthcare.entity.Hospital;
import com.fastcampus.healthcare.model.HospitalRequest;
import com.fastcampus.healthcare.model.HospitalResponse;
import com.fastcampus.healthcare.repository.HospitalRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HospitalServiceImplTest {
  @Mock
  private HospitalRepository hospitalRepository;
  @Mock
  private CacheService cacheService;

  @InjectMocks
  private HospitalServiceImpl hospitalService;

  private HospitalRequest hospitalRequest;
  private Hospital hospital;
  private HospitalResponse hospitalResponse;

  @BeforeEach
  void setUp() {
    hospitalRequest = HospitalRequest.builder()
        .name("Test Hospital")
        .address("123 Test St")
        .phone("1234567890")
        .email("test@hospital.com")
        .build();

    hospital = Hospital.builder()
        .id(1L)
        .name("Test Hospital")
        .address("123 Test St")
        .phone("1234567890")
        .email("test@hospital.com")
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();

    hospitalResponse = HospitalResponse.builder()
        .id(1L)
        .name("Test Hospital")
        .address("123 Test St")
        .phone("1234567890")
        .email("test@hospital.com")
        .build();
  }

  @Test
  void createHospital_shouldReturnHospitalResponse() {
    when(hospitalRepository.save(any(Hospital.class))).thenReturn(hospital);

    HospitalResponse result = hospitalService.create(hospitalRequest);

    assertNotNull(result);
    assertEquals(hospitalResponse.getName(), result.getName());
    assertEquals(hospitalResponse.getAddress(), result.getAddress());
    assertEquals(hospitalResponse.getPhone(), result.getPhone());
    assertEquals(hospitalResponse.getEmail(), result.getEmail());

    verify(hospitalRepository, times(1)).save(any(Hospital.class));
  }
}