package com.studyset.dto.dues;

import lombok.Getter;

@Getter
public class DuesInfo {
    private Long paidMemberCount; // 납부 인원
    private Double totalDuesAmount; // 총 회비 금액

    public DuesInfo(Long paidMemberCount, Double totalDuesAmount) {
        this.paidMemberCount = paidMemberCount;
        this.totalDuesAmount = totalDuesAmount;
    }

}
