package com.soaeng.happyhouse.user.service;

import com.soaeng.happyhouse.user.dto.request.UserRequestDto;
import com.soaeng.happyhouse.user.dto.response.UserResponseDto;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    Long registerUser(UserRequestDto dto);

    boolean isUsernameAvailable(String username);

    UserResponseDto readUser();

    Long updateUser(UserRequestDto dto, MultipartFile file);

    void deleteUser(UserRequestDto dto);
}
