package com.soaeng.happyhouse.user.repository;

import com.soaeng.happyhouse.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    // 자체 로그인 회원 가입 (존재 여부)
    Boolean existsByUsername(String username);

    // 소셜 회원 조회
    Optional<UserEntity> findByUsernameAndIsSocial(String username, Boolean social);

    // 자체 로그인 회원 정보 수정
    Optional<UserEntity> findByUsernameAndIsLockAndIsSocial(String username, Boolean isLock, Boolean isSocial);

    // 자체/소셜 유저 정보 조회
    Optional<UserEntity> findByUsernameAndIsLock(String username, Boolean isLock);

    // 자체/소셜 로그인 회원 탈퇴
    @Transactional
    void deleteByUsername(String username);
}
