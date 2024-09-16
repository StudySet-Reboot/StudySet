package com.studyset.domain;

import com.studyset.dto.dues.PaymentDto;
import com.studyset.web.form.PaymentForm;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/*사용 내역*/
@Entity
@NoArgsConstructor
public class Payment extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    private LocalDate paymentDate;

    @Column(nullable = false)
    private Double price;

    private String description;

    @Builder
    public Payment(Group group, LocalDate paymentDate, Double price, String description) {
        this.group = group;
        this.paymentDate = paymentDate;
        this.price = price;
        this.description = description;
    }

    public PaymentDto toPaymentDto() {
        return PaymentDto.builder()
                .id(id)
                .description(description)
                .paymentDate(paymentDate)
                .price(price)
                .build();
    }

    public static Payment from(PaymentForm paymentForm, Group group) {
        return Payment.builder()
                .group(group)
                .description(paymentForm.getDescription())
                .paymentDate(paymentForm.getPaymentDate())
                .price(paymentForm.getPrice())
                .build();
    }

}
