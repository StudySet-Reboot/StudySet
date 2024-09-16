package com.studyset.dto.dues;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
public class PaymentDto {
    private Long id;
    private Double price;
    private LocalDate paymentDate;
    private String description;

    @Builder
    public PaymentDto(Long id, Double price, LocalDate paymentDate, String description) {
        this.id = id;
        this.price = price;
        this.paymentDate = paymentDate;
        this.description = description;
    }

}
