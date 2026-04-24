package com.soaeng.happyhouse.board.service;

import com.soaeng.happyhouse.board.dto.request.BoardParamDto;
import com.soaeng.happyhouse.board.dto.request.ReplyRequestDto;
import com.soaeng.happyhouse.board.dto.response.BoardDto;
import com.soaeng.happyhouse.board.dto.response.ReplyDto;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.List;
import java.util.Map;

public interface BoardService {

    Long createBoard(MultipartHttpServletRequest request, String username);

    BoardDto getBoard(Long id, String username);

    void updateBoard(Long id, MultipartHttpServletRequest request);

    void deleteBoard(Long id);

    Map<String, Object> getBoardList(BoardParamDto param);

    ReplyDto createReply(ReplyRequestDto dto, String username);

    void updateReply(Long id, String content, String username);

    void deleteReply(Long id, String username);
}
