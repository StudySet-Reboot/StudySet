package com.studyset.api.exception;

import com.studyset.api.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotExist.class)
    public ResponseEntity<ErrorResponse> handleUserNotExist(UserNotExist ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("USER_NOT_EXIST")
                .message("해당 유저가 없습니다")
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(GroupCodeError.class)
    @ResponseBody
    public ResponseEntity<Map<String, String>> handleGroupCodeError(GroupCodeError ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", ex.getMessage());
        return ResponseEntity.status(ex.statusCode()).body(errorResponse);
    }
}

