package com.studyset.web.form;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class PaymentForm {
    @Min(value = 0, message = "납부 금액을 입력해주세요")
    private Double price;
    @NotNull(message = "사용 일자를 입력해주세요")
    private LocalDate paymentDate;
    @NotBlank(message = "사용 내역 상세를 작성해주세요")
    private String description;
}
