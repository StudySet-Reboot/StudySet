package com.studyset.exception;

public class GroupNotExist extends RuntimeException{
    private static final String message = "해당 그룹이 존재하지 않습니다";

    public GroupNotExist() {
        super(message);
    }
}
