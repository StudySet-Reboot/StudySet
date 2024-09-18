package com.studyset.exception;

import com.studyset.exception.response.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotExist.class)
    public ResponseEntity<ErrorResponse> handleUserNotExist(UserNotExist ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("USER_NOT_EXIST")
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(ex.statusCode()).body(errorResponse);
    }

    @ExceptionHandler(GroupCodeError.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleGroupCodeError(GroupCodeError ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(ex.statusCode())
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(ex.statusCode()).body(errorResponse);
    }
}

