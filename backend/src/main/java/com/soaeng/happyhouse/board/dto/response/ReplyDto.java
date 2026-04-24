package com.soaeng.happyhouse.board.dto.response;

import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
public class ReplyDto {

    private Long id;
    private Long boardId;
    private String username;
    private String nickname;
    private String content;
    private LocalDateTime createdDate;
    private boolean sameUser;

    public void setSameUser(boolean sameUser) { this.sameUser = sameUser; }
}
