package com.studyset.api.exception;

public class AlreadyJoin extends BaseException{
    private static final String message = "이미 가입한 그룹입니다";

    public AlreadyJoin() {
        super(message);
    }

    @Override
    public int statusCode() {
        return 400;
    }
}
