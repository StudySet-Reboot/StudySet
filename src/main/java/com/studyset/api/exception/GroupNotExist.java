package com.studyset.api.exception;

public class GroupNotExist extends BaseException{
    private static final String message = "해당 그룹이 존재하지 않습니다";

    public GroupNotExist() {
        super(message);
    }

    @Override
    public int statusCode() {
        return 404;
    }
}
