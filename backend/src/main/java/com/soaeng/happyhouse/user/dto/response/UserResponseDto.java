package com.soaeng.happyhouse.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class UserResponseDto {
    private String username;
    private String nickname;
    private String email;
    private String profileImageUrl;
}
