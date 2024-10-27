package com.studyset.exception.hanlder;

import com.studyset.exception.*;
import com.studyset.exception.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {

    @ResponseBody
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(BaseException e) {
        log.error("Error:: " + e.getMessage());
        ErrorResponse body = ErrorResponse.of(e);

        return ResponseEntity.status(e.statusCode())
                .body(body);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> invalidRequestHandler(MethodArgumentNotValidException e) {
        log.error("Error:: " + e.getMessage());

        ErrorResponse response = ErrorResponse.builder()
                .code("400")
                .message(e.getFieldError().getDefaultMessage())
                .build();

        for (FieldError fieldError : e.getFieldErrors()) {
            response.addValidation(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

}
