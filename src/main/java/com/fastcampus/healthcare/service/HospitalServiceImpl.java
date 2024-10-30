package com.fastcampus.healthcare.service;

import com.fastcampus.healthcare.common.exception.ResourceNotFoundException;
import com.fastcampus.healthcare.entity.Hospital;
import com.fastcampus.healthcare.model.HospitalRequest;
import com.fastcampus.healthcare.model.HospitalResponse;
import com.fastcampus.healthcare.repository.HospitalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class HospitalServiceImpl implements
    HospitalService {

  private final HospitalRepository hospitalRepository;
  private final CacheService cacheService;

  private static String HOSPITAL_CACHE_KEY = "cache:key:hospital:";

  @Override
  public Page<HospitalResponse> search(String keyword, Pageable pageable) {
    return null;
  }

  @Override
  public HospitalResponse get(Long id) {
    String key = HOSPITAL_CACHE_KEY + id;
    return cacheService.get(key, HospitalResponse.class)
        .orElseGet(() -> hospitalRepository.findById(id)
            .map(this::convertToResponse)
            .orElseThrow(() -> new ResourceNotFoundException("Hospital with id " + id + " is not found")));
  }

  @Override
  public HospitalResponse update(Long id, HospitalRequest hospitalRequest) {
    Hospital hospital = hospitalRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Hospital with id " + id + " is not found"));
    updateHospitalFromRequest(hospital, hospitalRequest);

    hospitalRepository.save(hospital);
    return convertToResponse(hospital);
  }

  @Override
  public HospitalResponse create(HospitalRequest hospitalRequest) {
    Hospital hospital = Hospital.builder().build();
    updateHospitalFromRequest(hospital, hospitalRequest);

    hospitalRepository.save(hospital);
    return convertToResponse(hospital);
  }

  @Override
  public void delete(Long id) {
    Hospital hospital = hospitalRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Hospital with id " + id + " is not found"));
    hospitalRepository.delete(hospital);
  }

  private HospitalResponse convertToResponse(Hospital hospital) {
    return HospitalResponse.builder()
        .id(hospital.getId())
        .name(hospital.getName())
        .address(hospital.getAddress())
        .phone(hospital.getPhone())
        .email(hospital.getEmail())
        .description(hospital.getDescription())
        .build();
  }

  private void updateHospitalFromRequest(Hospital hospital, HospitalRequest request) {
    hospital.setName(request.getName());
    hospital.setAddress(request.getAddress());
    hospital.setPhone(request.getPhone());
    hospital.setEmail(request.getEmail());
    hospital.setDescription(request.getDescription());
  }
}
