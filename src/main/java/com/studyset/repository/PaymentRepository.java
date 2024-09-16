package com.studyset.repository;

import com.studyset.domain.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Page<Payment> findByGroupId(Long groupId, Pageable pageable);

}
