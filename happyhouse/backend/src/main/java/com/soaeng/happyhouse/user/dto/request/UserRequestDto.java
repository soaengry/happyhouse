package com.soaeng.happyhouse.user.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequestDto {

    private String username;
    
    private String nickname;

    private String email;

    private String profileImageUrl;

    private boolean isChangedImage;
}
