package com.studyset.exception.response;

// TODO: 예외 처리 개선 필요
public enum ErrorCode {

    UNAUTHORIZED_USER("UNAUTHORIZED_USER", "로그인이 필요한 서비스입니다."),
    FORBIDDEN_USER("USER_NOT_JOINED", "해당 그룹에 접근 권한이 없습니다."),
    USER_NOT_FOUND("USER_NOT_FOUND", "해당 유저가 존재하지 않습니다"),
    GROUP_NOT_FOUND("GROUP_NOT_FOUND", "해당 그룹이 존재하지 않습니다."),
    TASK_NOT_FOUND("TASK_NOT_FOUND", "해당 과제가 존재하지 않습니다."),
    INVALID_REQUEST("INVALID_REQUEST", "잘못된 요청값입니다."),
    ALREADY_JOIN_GROUP("ALREADY_JOIN_GROUP", "이미 가입한 그룹입니다."),
    GROUP_ALREADY_EXIST("GROUP_ALREADY_EXIST", "이미 존재하는 그룹입니다."),
    INVALID_GROUP_CODE("INVALID_GROUP_CODE", "그룹 코드가 일치하지 않습니다."),
    INVALID_END_DATE("INVALID_END_DATE", "시작시간은 종료시간보다 앞서야 합니다."),
    ;


    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
