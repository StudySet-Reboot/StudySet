package com.studyset.service;

import com.studyset.api.exception.GroupNotExist;
import com.studyset.domain.Group;
import com.studyset.domain.Payment;
import com.studyset.dto.dues.PaymentDto;
import com.studyset.repository.GroupRepository;
import com.studyset.repository.PaymentRepository;
import com.studyset.web.form.PaymentForm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private GroupRepository groupRepository;

    @InjectMocks
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Mockito 초기화
    }

    @Test
    void getPayments_success() {
        // Given
        Long groupId = 1L;
        Pageable pageable = mock(Pageable.class);
        Payment payment = mock(Payment.class);
        when(paymentRepository.findByGroupId(groupId, pageable))
                .thenReturn(new PageImpl<>(Collections.singletonList(payment)));
        when(payment.toPaymentDto()).thenReturn(new PaymentDto());

        // When
        Page<PaymentDto> result = paymentService.getPayments(groupId, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(paymentRepository, times(1)).findByGroupId(groupId, pageable);
    }

    @Test
    @DisplayName("폼을 입력받아 회비 사용 내역 추가 성공")
    void addPayment_success() {
        // Given
        Long groupId = 1L;

        PaymentForm paymentForm = mock(PaymentForm.class);
        Group group = mock(Group.class);
        when(groupRepository.findGroupById(groupId)).thenReturn(Optional.of(group));

        // When
        paymentService.addPayment(groupId, paymentForm);

        // Then
        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(groupRepository, times(1)).findGroupById(groupId);
    }

    @Test
    @DisplayName("사용 내역 추가 시 그룹이 존재하지 않으면 GroupNotExist exception 발생")
    void addPayment_fail_WhenGroupDoesNotExist() {
        // Given
        Long groupId = 1L;
        PaymentForm paymentForm = mock(PaymentForm.class);

        when(groupRepository.findGroupById(groupId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(GroupNotExist.class, () -> paymentService.addPayment(groupId, paymentForm));
        verify(groupRepository, times(1)).findGroupById(groupId);
        verify(paymentRepository, never()).save(any(Payment.class));
    }
}
