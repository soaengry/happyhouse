package com.soaeng.happyhouse.user.service;

import com.soaeng.happyhouse.jwt.service.JwtService;
import com.soaeng.happyhouse.user.dto.CustomOAuth2User;
import com.soaeng.happyhouse.user.dto.request.UserRequestDto;
import com.soaeng.happyhouse.user.dto.response.*;
import com.soaeng.happyhouse.user.entity.ProviderType;
import com.soaeng.happyhouse.user.entity.RoleType;
import com.soaeng.happyhouse.user.entity.UserEntity;
import com.soaeng.happyhouse.user.repository.UserRepository;
import com.soaeng.happyhouse.util.FileStorageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends DefaultOAuth2UserService implements UserService {

    private final FileStorageUtil fileStorageUtil;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        // 부모 메소드 호출
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 데이터
        List<GrantedAuthority> authorities;

        // provider 제공자별 데이터 획득
        String registrationId = userRequest.getClientRegistration().getRegistrationId().toUpperCase();
        ProviderType provider = ProviderType.valueOf(registrationId);

        OAuth2ResponseDto oAuth2ResponseDto = switch (provider) {
            case GOOGLE -> new GoogleResponseDto(oAuth2User.getAttributes());
            case KAKAO -> new KakaoResponseDto(oAuth2User.getAttributes());
            case NAVER -> new NaverResponseDto(oAuth2User.getAttributes());
        };

        // 데이터베이스 조회 -> 존재하면 업데이트, 없으면 신규 가입
        Optional<UserEntity> entity = userRepository.findByUsername(oAuth2ResponseDto.getUsername());
        UserEntity user;
        RoleType role = RoleType.USER;

        if (entity.isPresent()) {
            user = entity.get();
        } else {
            // 신규 유저 추가
            UserEntity newUserEntity = UserEntity.builder()
                    .username(oAuth2ResponseDto.getUsername())
                    .isLock(false)
                    .providerType(ProviderType.valueOf(registrationId))
                    .roleType(role)
                    .nickname(oAuth2ResponseDto.getNickname())
                    .email(oAuth2ResponseDto.getEmail())
                    .profileImageUrl(oAuth2ResponseDto.getProfileImage() != null ?
                            getPhotoUrl(oAuth2ResponseDto.getProfileImage()) : null)
                    .build();
            user = userRepository.save(newUserEntity);
        }

        authorities = List.of(new SimpleGrantedAuthority(role.name()));

        return new CustomOAuth2User(oAuth2User.getAttributes(), authorities, user.getUsername());
    }

    private String getPhotoUrl(String url) {
        return fileStorageUtil.saveProfileImage(url);
    }

    // 자체/소셜 유저 정보 조회
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

        // 본인만 수정 가능 검증
        String sessionUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!sessionUsername.equals(dto.getUsername())) {
            throw new AccessDeniedException("본인 계정만 수정 가능");
        }

        // 조회
        UserEntity entity = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException(dto.getUsername()));

        // 회원 정보 수정
        entity.updateNickname(dto.getNickname());

        // 프로필 사진이 변경된 경우만 파일 수정
        if (dto.isChangedImage()) {
            String newImageUrl = changeImageFile("profile", entity.getProfileImageUrl(), file);
            entity.updateProfileImageUrl(newImageUrl);
        }

        return entity.getId();
    }

    private String changeImageFile(String folderName, String fileUrl, MultipartFile file) {
        if (fileUrl != null && !fileUrl.equals("null")) {
            fileStorageUtil.deleteFiles(folderName, List.of(fileUrl));
        }
        if (!file.isEmpty()) {
            List<String> newImageUrl = fileStorageUtil.saveFiles(folderName, List.of(file));
            return newImageUrl.get(0);
        }

        return null;
    }

    // 자체/소셜 로그인 회원 탈퇴
    @Transactional
    public void deleteUser(UserRequestDto dto) throws AccessDeniedException {

        // 본인 및 어드민만 삭제 가능 검증
        SecurityContext context = SecurityContextHolder.getContext();
        String sessionUsername = context.getAuthentication().getName();
        String sessionRole = context.getAuthentication().getAuthorities().iterator().next().getAuthority();

        boolean isOwner = sessionUsername.equals(dto.getUsername());
        boolean isAdmin = sessionRole.equals("ROLE_" + RoleType.ADMIN.name());

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("본인 혹은 관리자만 삭제할 수 있습니다.");
        }

        // 유저 제거
        userRepository.deleteByUsername(dto.getUsername());

        // Refresh 토큰 제거
        jwtService.removeRefreshUser(dto.getUsername());
    }
}
