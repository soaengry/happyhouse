package com.soaeng.happyhouse.jwt.service;

public interface JwtService {

    // JWT Refresh 토큰 발급 후 저장 메소드
    void addRefresh(String username, String refreshToken);

    // JWT Refresh 존재 확인 메소드
    Boolean existsRefresh(String refreshToken);

    // JWT Refresh 토큰 삭제 메소드
    void removeRefresh(String refreshToken);

    // 특정 유저 Refresh 토큰 모두 삭제 (탈퇴)
    void removeRefreshUser(String username);

}
