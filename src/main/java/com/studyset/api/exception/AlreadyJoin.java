package com.studyset.exception;

public class AlreadyJoin extends RuntimeException{
    private static final String message = "이미 가입한 그룹입니다";

    public AlreadyJoin() {
        super(message);
    }
}
