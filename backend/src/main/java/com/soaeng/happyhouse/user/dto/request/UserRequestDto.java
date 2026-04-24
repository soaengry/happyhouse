package com.soaeng.happyhouse.user.dto.request;

import lombok.Data;

@Data
public class UserRequestDto {
    private String username;
    private String password;
    private String nickname;
    private String email;
    private String profileImageUrl;
    private boolean isChangedImage;
}
