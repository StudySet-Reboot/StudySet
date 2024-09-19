package com.studyset.exception;

import com.studyset.exception.response.ErrorCode;
import org.springframework.http.HttpStatus;

public class GroupCodeError extends BaseException {
    private static final String message = "그룹 코드가 일치하지 않습니다.";

    public GroupCodeError() {
        super(ErrorCode.INVALID_GROUP_CODE);
    }

    @Override
    public HttpStatus statusCode() {
        return HttpStatus.BAD_REQUEST;
    }
}
