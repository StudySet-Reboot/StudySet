package com.studyset.exception;

import com.studyset.exception.response.ErrorCode;
import org.springframework.http.HttpStatus;

public class AlreadyJoin extends BaseException{

    public AlreadyJoin() {
        super(ErrorCode.ALREADY_JOIN_GROUP);
    }

    @Override
    public HttpStatus statusCode() {
        return HttpStatus.CONFLICT;
    }
}
