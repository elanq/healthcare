package com.fastcampus.healthcare.repository;

import com.fastcampus.healthcare.common.constant.PaymentStatus;
import com.fastcampus.healthcare.entity.Payment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
  @Query(value = "SELECT * FROM payment WHERE appointment_id = :appointmentId FOR UPDATE", nativeQuery = true)
  Optional<Payment> findByAppointmentIdAndLock(Long appointmentId);

  Optional<Payment> findByAppointmentId(Long appointmentId);

  Optional<Payment> findByXenditInvoiceId(String xenditInvoiceId);

  @Query(value = "SELECT * FROM payment WHERE id = :id FOR UPDATE", nativeQuery = true)
  Optional<Payment> findByIdAndLock(@Param("id") Long id);

  @Query("SELECT p FROM Payment p WHERE p.appointmentId = :appointmentId AND p.status = 'COMPLETED'")
  Optional<Payment> findCompletedPaymentByAppointmentId(@Param("appointmentId") Long appointmentId);

  List<Payment> findByStatus(PaymentStatus status);
}
