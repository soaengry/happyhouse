package com.soaeng.happyhouse.user.entity;

import lombok.Getter;

@Getter
public enum ProviderType {

    NAVER("네이버"),
    GOOGLE("구글"),
    KAKAO("카카오");
    private final String description;

    ProviderType(String description) {
        this.description = description;
    }

}
