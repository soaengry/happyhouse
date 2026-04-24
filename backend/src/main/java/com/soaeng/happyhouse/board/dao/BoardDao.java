package com.soaeng.happyhouse.board.dao;

import com.soaeng.happyhouse.board.dto.request.BoardParamDto;
import com.soaeng.happyhouse.board.dto.response.BoardDto;
import com.soaeng.happyhouse.board.dto.response.BoardFileDto;
import com.soaeng.happyhouse.board.dto.response.ReplyDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface BoardDao {

    /* 게시글 CRUD */
    void insertBoard(Map<String, Object> param);  // useGeneratedKeys → param에 id 세팅

    void updateBoard(Map<String, Object> param);

    void deleteBoard(Long id);

    BoardDto getBoardDetail(Long id);

    List<BoardDto> getBoardList(BoardParamDto param);

    int getBoardCount(BoardParamDto param);

    /* 파일 */
    void insertBoardFile(Map<String, Object> param);

    List<BoardFileDto> getBoardFileList(Long boardId);

    List<String> getBoardFileUrls(Long boardId);

    void deleteBoardFiles(Long boardId);

    /* 조회수 중복 방지 */
    int countUserRead(Map<String, Object> param);

    void insertUserRead(Map<String, Object> param);

    void updateReadCount(Long id);

    /* 댓글 */
    void insertReply(Map<String, Object> param);  // useGeneratedKeys → param에 id 세팅

    void updateReply(Map<String, Object> param);

    void deleteReply(Long id);

    void deleteRepliesByBoard(Long boardId);

    ReplyDto getReplyDetail(Long id);

    List<ReplyDto> getReplyList(Long boardId);
}
