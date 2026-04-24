package com.soaeng.happyhouse.board.dto.response;

import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
public class BoardFileDto {

    private Long id;
    private Long boardId;
    private String fileName;
    private Long fileSize;
    private String fileContentType;
    private String fileUrl;
    private LocalDateTime createdDate;
}
