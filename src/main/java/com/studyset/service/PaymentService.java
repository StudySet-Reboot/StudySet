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

    /**
     *  그룹의 회비 사용 내역 목록을 페이지로 반환합니다.
     *
     * @param groupId 그룹의 ID
     * @param pageable 페이징 정보
     * @return Page<PaymentDto> 회비 사용 내역 목록 DTO
     */
    @Transactional(readOnly = true)
    public Page<PaymentDto> getPayments(Long groupId, Pageable pageable) {
        return paymentRepository.findByGroupId(groupId, pageable)
                .map(Payment::toPaymentDto);
    }

    /**
     * 회비 사용 내역을 추가합니다.
     *
     * @param groupId     회비 사용 내역이 추가될 그룹의 ID
     * @param paymentForm 회비 사용 내역을 담은 form 객체
     */
    public void addPayment(Long groupId, PaymentForm paymentForm) {
        Group group = groupRepository.findGroupById(groupId)
                .orElseThrow(GroupNotExist::new);
        Payment payment = Payment.from(paymentForm, group);
        paymentRepository.save(payment);
    }

}
