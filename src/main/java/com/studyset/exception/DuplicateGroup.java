package com.studyset.exception;

import com.studyset.exception.response.ErrorCode;
import org.springframework.http.HttpStatus;

public class DuplicateGroup extends BaseException {

    public DuplicateGroup() {
        super(ErrorCode.GROUP_ALREADY_EXIST);
    }

    @Override
    public HttpStatus statusCode() {
        return HttpStatus.CONFLICT;
    }
}
