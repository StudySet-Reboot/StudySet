package com.studyset.domain.enumerate;

public enum ProviderCategory {
    GOOGLE("구글"), KAKAO("카카오");
    private String categoryName;
    ProviderCategory(String categoryName) {
        this.categoryName = categoryName;
    }
}
