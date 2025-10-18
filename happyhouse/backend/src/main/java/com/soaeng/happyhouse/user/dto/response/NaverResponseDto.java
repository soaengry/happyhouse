package com.soaeng.happyhouse.user.dto.response;

import java.util.Map;

public class NaverResponseDto implements OAuth2ResponseDto {

    private final Map<String, Object> response;

    public NaverResponseDto(Map<String, Object> attribute) {
        this.response = (Map<String, Object>) attribute.get("response");
    }

    @Override
    public String getUsername() {
        return "NAVER_" + response.get("id");
    }

    @Override
    public String getEmail() {
        return (String) response.get("email");
    }

    @Override
    public String getNickname() {
        return (String) response.get("name");
    }

    @Override
    public String getPicture() {
        return (String) response.get("profile_image");
    }
}
