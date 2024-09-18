package com.studyset.exception;

import com.studyset.exception.response.ErrorCode;
import org.springframework.http.HttpStatus;

public class GroupNotExist extends BaseException{
    private static final String message = "해당 그룹이 존재하지 않습니다";

    public GroupNotExist() {
        super(ErrorCode.GROUP_NOT_FOUND);
    }

    @Override
    public HttpStatus statusCode() {
        return HttpStatus.NOT_FOUND;
    }
}
