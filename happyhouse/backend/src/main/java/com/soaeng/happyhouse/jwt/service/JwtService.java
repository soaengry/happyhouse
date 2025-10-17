package com.soaeng.happyhouse.jwt.service;

import com.soaeng.happyhouse.jwt.dto.request.RefreshRequestDto;
import com.soaeng.happyhouse.jwt.dto.response.JwtResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface JwtService {

    // JWT Refresh 토큰 발급 후 저장 메소드
    void addRefresh(String username, String refreshToken);

    // JWT Refresh 존재 확인 메소드
    Boolean existsRefresh(String refreshToken);

    // JWT Refresh 토큰 삭제 메소드
    void removeRefresh(String refreshToken);

    // 특정 유저 Refresh 토큰 모두 삭제 (탈퇴)
    void removeRefreshUser(String username);

    // Refresh 토큰으로 Access 토큰 재발급 (Rotate 포함)
    JwtResponseDto refreshRotate(RefreshRequestDto dto);

    JwtResponseDto cookie2Header(HttpServletRequest request, HttpServletResponse response);
}
