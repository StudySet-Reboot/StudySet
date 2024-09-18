package com.studyset.exception;

import com.studyset.exception.response.ErrorCode;
import org.springframework.http.HttpStatus;

public class InvalidEndDate extends BaseException{

    public InvalidEndDate() {
        super(ErrorCode.INVALID_END_DATE);
    }

    @Override
    public HttpStatus statusCode() {
        return HttpStatus.BAD_REQUEST;
    }
}
