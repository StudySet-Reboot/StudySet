package com.studyset.web.form;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class DuesForm {
    @NotNull
    private Long userId;
    @Min(value = 0, message = "납부 금액을 입력해주세요")
    private Double price;
    private LocalDate duesDate;
}
