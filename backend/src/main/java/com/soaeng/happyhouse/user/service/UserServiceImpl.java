package com.soaeng.happyhouse.user.service;

import com.soaeng.happyhouse.user.dto.request.UserRequestDto;
import com.soaeng.happyhouse.user.dto.response.UserResponseDto;
import com.soaeng.happyhouse.user.entity.RoleType;
import com.soaeng.happyhouse.user.entity.UserEntity;
import com.soaeng.happyhouse.user.repository.UserRepository;
import com.soaeng.happyhouse.jwt.service.JwtService;
import com.soaeng.happyhouse.util.FileStorageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {

    private final FileStorageUtil fileStorageUtil;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    private static final String PROFILE_FOLDER = "profile";

    @Transactional
    @Override
    public Long registerUser(UserRequestDto dto) {
        UserEntity entity = UserEntity.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .isLock(false)
                .roleType(RoleType.USER)
                .nickname(dto.getNickname())
                .email(dto.getEmail())
                .build();
        return userRepository.save(entity).getId();
    }

    @Transactional(readOnly = true)
    @Override
    public boolean isUsernameAvailable(String username) {
        return userRepository.findByUsername(username).isEmpty();
    }

    @Transactional(readOnly = true)
    @Override
    public UserResponseDto readUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity entity = userRepository.findByUsernameAndIsLock(username, false)
                .orElseThrow(() -> new UsernameNotFoundException("해당 유저를 찾을 수 없습니다: " + username));

        return new UserResponseDto(username, entity.getNickname(), entity.getEmail(), entity.getProfileImageUrl());
    }

    @Transactional
    @Override
    public Long updateUser(UserRequestDto dto, MultipartFile file) throws AccessDeniedException {
        String sessionUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!sessionUsername.equals(dto.getUsername())) {
            throw new AccessDeniedException("본인 계정만 수정 가능");
        }

        UserEntity entity = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException(dto.getUsername()));

        entity.updateNickname(dto.getNickname());

        if (dto.isChangedImage()) {
            String newImageUrl = changeImageFile(entity.getProfileImageUrl(), file);
            entity.updateProfileImageUrl(newImageUrl);
        }

        return entity.getId();
    }

    @Transactional
    @Override
    public void deleteUser(UserRequestDto dto) throws AccessDeniedException {
        SecurityContext context = SecurityContextHolder.getContext();
        String sessionUsername = context.getAuthentication().getName();
        String sessionRole = context.getAuthentication().getAuthorities().iterator().next().getAuthority();

        boolean isOwner = sessionUsername.equals(dto.getUsername());
        boolean isAdmin = sessionRole.equals("ROLE_" + RoleType.ADMIN.name());

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("본인 혹은 관리자만 삭제할 수 있습니다.");
        }

        userRepository.deleteByUsername(dto.getUsername());
        jwtService.removeRefreshUser(dto.getUsername());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsernameAndIsLock(username, false)
                .orElseThrow(() -> new UsernameNotFoundException("해당 유저를 찾을 수 없습니다: " + username));
    }

    private String changeImageFile(String existingUrl, MultipartFile file) {
        if (existingUrl != null && !existingUrl.equals("null")) {
            fileStorageUtil.deleteFiles(PROFILE_FOLDER, List.of(existingUrl));
        }
        if (file != null && !file.isEmpty()) {
            return fileStorageUtil.saveFiles(PROFILE_FOLDER, List.of(file)).get(0);
        }
        return null;
    }
}
