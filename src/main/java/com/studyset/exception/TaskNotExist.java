package com.studyset.api.exception;

public class TaskNotExist extends BaseException{
    private static final String message = "해당 과제가 존재하지 않습니다";

    public TaskNotExist() {
        super(message);
    }

    @Override
    public int statusCode() {
        return 404;
    }
}
