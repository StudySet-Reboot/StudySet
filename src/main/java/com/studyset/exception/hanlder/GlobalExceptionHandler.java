package com.studyset.exception.hanlder;

import com.studyset.exception.GroupCodeError;
import com.studyset.exception.UserNotExist;
import com.studyset.exception.response.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotExist.class)
    public ResponseEntity<ErrorResponse> handleUserNotExist(UserNotExist e) {
        ErrorResponse errorResponse = ErrorResponse.of(e);
        return ResponseEntity.status(e.statusCode()).body(errorResponse);
    }

    @ExceptionHandler(GroupCodeError.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleGroupCodeError(GroupCodeError e) {
        ErrorResponse errorResponse = ErrorResponse.of(e);
        return ResponseEntity.status(e.statusCode()).body(errorResponse);
    }
}

