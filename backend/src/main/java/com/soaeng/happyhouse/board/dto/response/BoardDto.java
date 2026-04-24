package com.soaeng.happyhouse.board.dto.response;

import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@ToString
public class BoardDto {

    private Long id;
    private String username;
    private String nickname;
    private String profileImageUrl;
    private String title;
    private String content;
    private int readCount;
    private LocalDateTime createdDate;
    private boolean sameUser;
    private List<BoardFileDto> fileList;
    private List<ReplyDto> replyList;

    public void setSameUser(boolean sameUser)            { this.sameUser = sameUser; }
    public void setFileList(List<BoardFileDto> fileList) { this.fileList = fileList; }
    public void setReplyList(List<ReplyDto> replyList)   { this.replyList = replyList; }
}
