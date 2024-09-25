package com.studyset.dto.dues;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DuesInfo {
    private int paidMemberCount; // 납부 인원
    private Double totalDuesAmount; // 총 회비 금액
}