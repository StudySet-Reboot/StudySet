package com.studyset.api.exception;

import java.util.HashMap;

public abstract class BaseException extends RuntimeException{
    private final HashMap<String, String> validation = new HashMap<>();
    public BaseException(String message) {
        super(message);
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public abstract int statusCode();
    public void addValidation(String fieldName, String errMessage){
        validation.put(fieldName, errMessage);
    }

    public HashMap<String, String> getValidation() {
        return validation;
    }
}
