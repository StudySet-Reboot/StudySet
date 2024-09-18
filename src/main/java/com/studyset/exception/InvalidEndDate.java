package com.studyset.exception;

public class InvalidEndDateException extends BaseException{

    private static final String message = "시작시간은 종료시간보다 앞서야 합니다.";

    public InvalidEndDateException() {
        super(message);
    }

    @Override
    public int statusCode() {
        return 400;
    }
}
