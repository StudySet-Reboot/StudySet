package com.studyset.exception.hanlder;

import com.studyset.exception.AlreadyJoin;
import com.studyset.exception.DuplicateGroup;
import com.studyset.exception.GroupNotExist;
import com.studyset.exception.InvalidEndDate;
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
    @ExceptionHandler(GroupNotExist.class)
    public ResponseEntity<ErrorResponse> notExistGroupRequest(GroupNotExist e){
        log.error("Error:: " + e.getMessage());
        ErrorResponse body = ErrorResponse.of(e);
        return ResponseEntity.status(e.statusCode())
                .body(body);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(AlreadyJoin.class)
    public ResponseEntity<ErrorResponse> alreadyJoinGroup(AlreadyJoin e){
        log.error("Error:: " + e.getMessage());
        ErrorResponse body = ErrorResponse.of(e);
        return ResponseEntity.status(e.statusCode())
                .body(body);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DuplicateGroup.class)
    public ResponseEntity<ErrorResponse> alreadyExistGroup(DuplicateGroup e){
        log.error("Error:: " + e.getMessage());
        ErrorResponse body = ErrorResponse.of(e);
        return ResponseEntity.status(e.statusCode())
                .body(body);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidEndDate.class)
    public ResponseEntity<ErrorResponse> invalidRequestHandler(InvalidEndDate e) {
        log.error("Error:: " + e.getMessage());
        ErrorResponse response = ErrorResponse.of(e);
        return ResponseEntity.status(e.statusCode())
                .body(response);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> invalidRequestHandler(MethodArgumentNotValidException e) {
        log.error("Error:: " + e.getMessage());
        ErrorResponse response = ErrorResponse.builder()
                .code("400")
                .message("잘못된 요청입니다.")
                .build();

        for (FieldError fieldError : e.getFieldErrors()) {
            response.addValidation(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

}
