package com.studyset.exception;

import com.studyset.exception.response.ErrorCode;
import org.springframework.http.HttpStatus;

public class TaskDeadlineException extends BaseException{
    public TaskDeadlineException() {
        super(ErrorCode.TASK_DEADLINE_PASSED);
    }

    @Override
    public HttpStatus statusCode() {
        return HttpStatus.FORBIDDEN;
    }
}
