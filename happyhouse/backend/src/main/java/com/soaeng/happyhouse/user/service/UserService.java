package com.soaeng.happyhouse.user.service;

import com.soaeng.happyhouse.user.dto.request.UserRequestDto;
import com.soaeng.happyhouse.user.entity.response.UserResponseDto;

public interface UserService {

    // 자체 로그인 회원 가입 (존재 여부)
    Boolean existUser(UserRequestDto dto);

    // 자체 로그인 회원 가입
    Long addUser(UserRequestDto dto);

    // 자체 로그인 회원 정보 수정
    Long updateUser(UserRequestDto dto);

    // 자체/소셜 로그인 회원 탈퇴
    void deleteUser(UserRequestDto dto);
    // 소셜 로그인 (매 로그인시 : 신규 = 가입, 기존 = 업데이트)

    // 자체/소셜 유저 정보 조회
    UserResponseDto readUser();
}
