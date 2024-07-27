package com.studyset.domain.enumerate;

public enum GroupCategory {
    PROGRAMMING("프로그래밍"), LANGUAGE("어학"), DESIGN("디자인"), BUSINESS("경엉"), JOBHUNTING("취업"), ETC("기타");
    private String categoryName;
    GroupCategory(String categoryName) {
        this.categoryName = categoryName;
    }
}
