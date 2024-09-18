package com.studyset.exception.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.studyset.exception.BaseException;
import lombok.Builder;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class ErrorResponse {

    private final String code;
    private final String message;
    private final Map<String, String> validation = new HashMap<>();

    public void addValidation(String fieldName, String errorMessage){
        this.validation.put(fieldName, errorMessage);
    }

    @Builder
    public ErrorResponse(String code, String message, Map<String, String> validation) {
        this.code = code;
        this.message = message;
        if(validation != null && !validation.isEmpty()){
            for(String key: validation.keySet()){
                this.validation.put(key, validation.get(key));
            }
        }
    }

    public static ErrorResponse of(BaseException e) {
        return ErrorResponse.builder()
                .code(e.getErrorCode())
                .message(e.getMessage())
                .validation(e.getValidation())
                .build();
    }
}