package com.studyset.exception;

import com.studyset.exception.response.ErrorCode;
import org.springframework.http.HttpStatus;

import java.util.HashMap;

public abstract class BaseException extends RuntimeException{

    private final ErrorCode errorCode;
    private final HashMap<String, String> validation = new HashMap<>();

    public BaseException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BaseException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public abstract HttpStatus statusCode();

    public String getErrorCode(){
        return errorCode.getCode();
    }

    @Override
    public String getMessage() {
        return errorCode.getMessage();
    }

    public void addValidation(String fieldName, String errMessage){
        validation.put(fieldName, errMessage);
    }

    public HashMap<String, String> getValidation() {
        return validation;
    }
}
