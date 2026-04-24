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

    // 회원가입
    @PostMapping("")
    public ResponseEntity<Long> registerUser(@RequestBody UserRequestDto dto) {
        return ResponseEntity.status(201).body(userService.registerUser(dto));
    }

    // 아이디 중복 확인 (true = 사용 가능)
    @PostMapping("/exist")
    public ResponseEntity<Boolean> checkUsernameAvailability(@RequestBody UserRequestDto dto) {
        return ResponseEntity.ok(userService.isUsernameAvailable(dto.getUsername()));
    }

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
        if (file != null && !file.isEmpty()) {
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
