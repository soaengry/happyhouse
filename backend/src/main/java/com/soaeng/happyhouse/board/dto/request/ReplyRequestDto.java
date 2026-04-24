package com.soaeng.happyhouse.board.dto.request;

import lombok.Data;

@Data
public class ReplyRequestDto {
    private Long boardId;
    private String content;
}
