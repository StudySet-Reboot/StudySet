package com.studyset.api.exception;

public class UserNotExist extends BaseException{
    private static final String message = "해당 유저가 존재하지 않습니다";

    public UserNotExist() {
        super(message);
    }

    @Override
    public int statusCode() {
        return 404;
    }
}

