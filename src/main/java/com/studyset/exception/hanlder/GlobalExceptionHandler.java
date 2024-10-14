package com.studyset.exception.hanlder;

import ch.qos.logback.core.model.processor.ModelHandlerException;
import com.studyset.exception.GroupCodeError;
import com.studyset.exception.UserNotExist;
import com.studyset.exception.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.HashMap;

@ControllerAdvice
@Slf4j
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

