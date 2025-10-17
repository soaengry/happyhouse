package com.soaeng.happyhouse.user.service;

import com.soaeng.happyhouse.jwt.service.JwtService;
import com.soaeng.happyhouse.user.dto.CustomOAuth2User;
import com.soaeng.happyhouse.user.dto.request.UserRequestDto;
import com.soaeng.happyhouse.user.entity.ProviderType;
import com.soaeng.happyhouse.user.entity.RoleType;
import com.soaeng.happyhouse.user.entity.UserEntity;
import com.soaeng.happyhouse.user.entity.response.*;
import com.soaeng.happyhouse.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends DefaultOAuth2UserService implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional(readOnly = true)
    @Override
    public Boolean existUser(UserRequestDto dto) {
        return userRepository.existsByUsername(dto.getUsername());
    }

    @Transactional
    @Override
    public Long addUser(UserRequestDto dto) {

        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("이미 유저가 존재합니다.");
        }

        UserEntity entity = UserEntity.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .isLock(false)
                .isSocial(false)
                .roleType(RoleType.USER)
                .nickname(dto.getNickname())
                .email(dto.getEmail())
                .build();

        return userRepository.save(entity).getId();
    }

    // 자체 로그인
    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserEntity entity = userRepository.findByUsernameAndIsLockAndIsSocial(username, false, false)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        return User.builder()
                .username(entity.getUsername())
                .password(entity.getPassword())
                .roles(entity.getRoleType().name())
                .accountLocked(entity.getIsLock())
                .build();
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        // 부모 메소드 호출
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 데이터
        Map<String, Object> attributes;
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
        Optional<UserEntity> entity = userRepository.findByUsernameAndIsSocial(oAuth2ResponseDto.getUsername(), true);
        UserEntity user;
        RoleType role = RoleType.USER;

        if (entity.isPresent()) {
            user = entity.get();
            // role 조회
            role = entity.get().getRoleType();

            // 기존 유저 업데이트
            UserRequestDto dto = new UserRequestDto();
            dto.setNickname(user.getNickname());
            dto.setEmail(user.getEmail());
            user.updateUser(dto);

            userRepository.save(user);
        } else {
            user = entity.get();
            // 신규 유저 추가
            UserEntity newUserEntity = UserEntity.builder()
                    .username(user.getUsername())
                    .password("")
                    .isLock(false)
                    .isSocial(true)
                    .socialProviderType(ProviderType.valueOf(registrationId))
                    .roleType(role)
                    .nickname(user.getNickname())
                    .email(user.getEmail())
                    .build();

            user = userRepository.save(newUserEntity);
        }

        authorities = List.of(new SimpleGrantedAuthority(role.name()));

        return new CustomOAuth2User(oAuth2User.getAttributes(), authorities, user.getUsername());
    }

    // 자체/소셜 유저 정보 조회
    @Transactional(readOnly = true)
    @Override
    public UserResponseDto readUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        UserEntity entity = userRepository.findByUsernameAndIsLock(username, false)
                .orElseThrow(() -> new UsernameNotFoundException("해당 유저를 찾을 수 없습니다: " + username));

        return new UserResponseDto(username, entity.getIsSocial(), entity.getNickname(), entity.getEmail());
    }

    @Transactional
    @Override
    public Long updateUser(UserRequestDto dto) throws AccessDeniedException {

        // 본인만 수정 가능 검증
        String sessionUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!sessionUsername.equals(dto.getUsername())) {
            throw new AccessDeniedException("본인 계정만 수정 가능");
        }

        // 조회
        UserEntity entity = userRepository.findByUsernameAndIsLockAndIsSocial(dto.getUsername(), false, false)
                .orElseThrow(() -> new UsernameNotFoundException(dto.getUsername()));

        // 회원 정보 수정
        entity.updateUser(dto);

        return userRepository.save(entity).getId();
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
