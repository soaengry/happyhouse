package com.soaeng.happyhouse.user.repository;

import com.soaeng.happyhouse.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    // 자체 로그인 회원 가입 (존재 여부)
    Boolean existsByUsername(String username);

    // 자체 로그인 회원 정보 수정
    Optional<UserEntity> findByUsernameAndIsLockAndIsSocial(String username, Boolean isLock, Boolean isSocial);

}
