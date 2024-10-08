package com.studyset.exception;

import com.studyset.exception.response.ErrorCode;
import org.springframework.http.HttpStatus;

public class ForbiddenAccess extends BaseException{

    public ForbiddenAccess(ErrorCode errorCode) {
        super(errorCode);
    }

    public ForbiddenAccess(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    @Override
    public HttpStatus statusCode() {
        return HttpStatus.FORBIDDEN;
    }
}
