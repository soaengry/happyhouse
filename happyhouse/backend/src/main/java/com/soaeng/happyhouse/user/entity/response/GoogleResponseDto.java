package com.soaeng.happyhouse.user.entity.response;

import java.util.Map;

public class GoogleResponseDto implements OAuth2ResponseDto {

    private final Map<String, Object> attributes;

    public GoogleResponseDto(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getUsername() {
        return "GOOGLE_" + attributes.get("sub");
    }

    @Override
    public String getEmail() {
        return attributes.get("email").toString();
    }

    @Override
    public String getNickname() {
        return attributes.get("name").toString();
    }

    @Override
    public String getPicture() {
        return attributes.get("picture").toString();
    }
}
