package com.studyset.dto.dues;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
public class DuesDto {

    private String userName;
    private Double price;
    private LocalDate duesDate;

    public DuesDto(String userName, Double price, LocalDate duesDate) {
        this.userName = userName;
        this.price = price;
        this.duesDate = duesDate;
    }

}
