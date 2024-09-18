package com.studyset.api.exception;

public class DuplicateGroup extends BaseException {
    private static final String message = "이미 존재하는 그룹입니다";

    public DuplicateGroup() {
        super(message);
    }

    @Override
    public int statusCode() {
        return 409;
    }
}
