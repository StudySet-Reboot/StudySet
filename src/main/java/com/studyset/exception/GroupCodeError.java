package com.studyset.api.exception;

public class GroupCodeError extends BaseException {
    private static final String message = "그룹 코드가 일치하지 않습니다.";

    public GroupCodeError() {
        super(message);
    }

    @Override
    public int statusCode() {
        return 404;
    }
}
