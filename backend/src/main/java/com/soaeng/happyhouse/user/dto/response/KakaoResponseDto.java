package com.soaeng.happyhouse.user.dto.response;

import java.util.Map;

public class KakaoResponseDto implements OAuth2ResponseDto {

    private final Map<String, Object> attributes;
    private final Map<String, Object> kakao_account;
    private final Map<String, Object> properties;

    public KakaoResponseDto(Map<String, Object> attributes) {
        this.attributes = attributes;
        this.kakao_account = (Map<String, Object>) attributes.get("kakao_account");
        this.properties = (Map<String, Object>) attributes.get("properties");
    }

    @Override
    public String getUsername() {
        return "KAKAO_" + attributes.get("id");
    }

    @Override
    public String getEmail() {
        return (String) kakao_account.get("email");
    }

    @Override
    public String getNickname() {
        return (String) properties.get("nickname");
    }

    @Override
    public String getProfileImage() {
        return (String) properties.get("thumbnail_image");
    }
}
