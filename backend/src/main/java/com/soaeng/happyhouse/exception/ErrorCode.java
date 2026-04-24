package com.soaeng.happyhouse.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    INVALID_TOKEN(400, "유효하지 않은 토큰입니다."),
    TOKEN_NOT_FOUND(400, "토큰을 찾을 수 없습니다."),
    ACCESS_DENIED(403, "접근 권한이 없습니다."),
    USER_NOT_FOUND(404, "사용자를 찾을 수 없습니다."),
    DUPLICATE_RESOURCE(409, "이미 존재하는 리소스입니다."),
    INTERNAL_ERROR(500, "서버 내부 오류가 발생했습니다.");

    private final int status;
    private final String message;
}
