package com.studyset.exception;

import com.studyset.exception.response.ErrorCode;
import org.springframework.http.HttpStatus;

public class UserNotExist extends BaseException{

    public UserNotExist() {
        super(ErrorCode.USER_NOT_FOUND);
    }

    public UserNotExist(ErrorCode errorCode) {
        super(errorCode);
    }

    @Override
    public HttpStatus statusCode() {
        return HttpStatus.NOT_FOUND;
    }
}

