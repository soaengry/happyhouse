package com.soaeng.happyhouse.user.service;

import com.soaeng.happyhouse.user.dto.CustomOAuth2User;
import com.soaeng.happyhouse.user.dto.response.*;
import com.soaeng.happyhouse.user.entity.ProviderType;
import com.soaeng.happyhouse.user.entity.RoleType;
import com.soaeng.happyhouse.user.entity.UserEntity;
import com.soaeng.happyhouse.user.repository.UserRepository;
import com.soaeng.happyhouse.util.FileStorageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthUserSyncService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final FileStorageUtil fileStorageUtil;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId().toUpperCase();
        ProviderType provider = ProviderType.valueOf(registrationId);

        OAuth2ResponseDto oAuth2ResponseDto = switch (provider) {
            case GOOGLE -> new GoogleResponseDto(oAuth2User.getAttributes());
            case KAKAO -> new KakaoResponseDto(oAuth2User.getAttributes());
            case NAVER -> new NaverResponseDto(oAuth2User.getAttributes());
        };

        UserEntity user = syncUser(oAuth2ResponseDto, registrationId);
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(RoleType.USER.name()));

        return new CustomOAuth2User(oAuth2User.getAttributes(), authorities, user.getUsername());
    }

    private UserEntity syncUser(OAuth2ResponseDto dto, String registrationId) {
        Optional<UserEntity> existing = userRepository.findByUsername(dto.getUsername());
        if (existing.isPresent()) {
            return existing.get();
        }

        String profileImageUrl = dto.getProfileImage() != null
                ? fileStorageUtil.saveProfileImage(dto.getProfileImage())
                : null;

        UserEntity newUser = UserEntity.builder()
                .username(dto.getUsername())
                .isLock(false)
                .providerType(ProviderType.valueOf(registrationId))
                .roleType(RoleType.USER)
                .nickname(dto.getNickname())
                .email(dto.getEmail())
                .profileImageUrl(profileImageUrl)
                .build();

        return userRepository.save(newUser);
    }
}
