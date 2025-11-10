package com.soaeng.happyhouse.user.controller;


import com.soaeng.happyhouse.user.dto.request.UserRequestDto;
import com.soaeng.happyhouse.user.dto.response.UserResponseDto;
import com.soaeng.happyhouse.user.service.UserService;
import com.soaeng.happyhouse.util.FileStorageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final FileStorageUtil fileStorageUtil;
    private final UserService userService;

    // 유저 정보
    @GetMapping(value = "")
    public UserResponseDto userMe() {
        return userService.readUser();
    }

    // 사용자 사진 조회 경로
    @GetMapping("/image")
    public ResponseEntity<Resource> viewUserProfileImage(String fileName) {
        return fileStorageUtil.getFile("profile", fileName);
    }

    // 유저 수정 (자체 로그인 유저만)
    @PutMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Long> updateUser(@ModelAttribute UserRequestDto dto, @RequestPart(required = false) MultipartFile file
    ) throws AccessDeniedException {
        log.debug(dto.toString());
        log.debug(file.getOriginalFilename());
        if (file != null && !file.isEmpty()) {
            log.debug(file.getOriginalFilename());
            dto.setChangedImage(true);
        }
        return ResponseEntity.status(200).body(userService.updateUser(dto, file));
    }

    // 유저 제거 (자체/소셜)
    @DeleteMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> deleteUser(@RequestBody UserRequestDto dto) throws AccessDeniedException {
        userService.deleteUser(dto);
        return ResponseEntity.status(200).body(true);
    }
}
