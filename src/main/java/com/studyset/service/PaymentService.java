package com.studyset.service;

import com.studyset.exception.GroupNotExist;
import com.studyset.domain.Group;
import com.studyset.domain.Payment;
import com.studyset.dto.dues.PaymentDto;
import com.studyset.repository.GroupRepository;
import com.studyset.repository.PaymentRepository;
import com.studyset.web.form.PaymentForm;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final GroupRepository groupRepository;

    @Transactional(readOnly = true)
    public Page<PaymentDto> getPayments(Long groupId, Pageable pageable) {
        return paymentRepository.findByGroupId(groupId, pageable)
                .map(Payment::toPaymentDto);
    }

    public void addPayment(Long groupId, PaymentForm paymentForm) {
        Group group = groupRepository.findGroupById(groupId)
                .orElseThrow(GroupNotExist::new);
        Payment payment = Payment.from(paymentForm, group);
        paymentRepository.save(payment);
    }

}
