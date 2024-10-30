package com.fastcampus.healthcare.service;

import com.fastcampus.healthcare.model.HospitalRequest;
import com.fastcampus.healthcare.model.HospitalResponse;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HospitalService {
  Page<HospitalResponse> search(String keyword, Pageable pageable);
  HospitalResponse get(Long id);
  HospitalResponse update(Long id, HospitalRequest hospitalRequest);
  HospitalResponse create(HospitalRequest hospitalRequest);
  void delete(Long id);
}
