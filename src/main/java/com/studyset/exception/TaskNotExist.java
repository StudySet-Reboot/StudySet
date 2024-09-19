package com.studyset.exception;

import com.studyset.exception.response.ErrorCode;
import org.springframework.http.HttpStatus;

public class TaskNotExist extends BaseException{

    public TaskNotExist() {
        super(ErrorCode.TASK_NOT_FOUND);
    }

    @Override
    public HttpStatus statusCode() {
        return HttpStatus.NOT_FOUND;
    }
}
