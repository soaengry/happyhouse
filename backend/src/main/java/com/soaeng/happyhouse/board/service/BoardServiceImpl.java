package com.soaeng.happyhouse.board.service;

import com.soaeng.happyhouse.board.dao.BoardDao;
import com.soaeng.happyhouse.board.dto.request.BoardParamDto;
import com.soaeng.happyhouse.board.dto.request.ReplyRequestDto;
import com.soaeng.happyhouse.board.dto.response.BoardDto;
import com.soaeng.happyhouse.board.dto.response.ReplyDto;
import com.soaeng.happyhouse.exception.BusinessException;
import com.soaeng.happyhouse.exception.ErrorCode;
import com.soaeng.happyhouse.util.FileStorageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private static final String BOARD_FOLDER = "board";

    private final BoardDao boardDao;
    private final FileStorageUtil fileStorageUtil;

    @Transactional
    @Override
    public Long createBoard(MultipartHttpServletRequest request, String username) {
        Map<String, Object> param = new HashMap<>();
        param.put("username", username);
        param.put("title", request.getParameter("title"));
        param.put("content", request.getParameter("content"));
        boardDao.insertBoard(param);

        Long boardId = ((Number) param.get("id")).longValue();
        saveFiles(boardId, request.getFiles("files"));
        return boardId;
    }

    @Transactional
    @Override
    public BoardDto getBoard(Long id, String username) {
        BoardDto board = boardDao.getBoardDetail(id);
        if (board == null) throw new BusinessException(ErrorCode.INTERNAL_ERROR);

        board.setFileList(boardDao.getBoardFileList(id));
        List<ReplyDto> replies = boardDao.getReplyList(id);
        if (username != null) {
            replies.forEach(r -> r.setSameUser(username.equals(r.getUsername())));
            board.setSameUser(username.equals(board.getUsername()));

            // 본인 글이 아니고 처음 조회하는 경우만 조회수 증가
            if (!board.isSameUser()) {
                Map<String, Object> readParam = Map.of("boardId", id, "username", username);
                if (boardDao.countUserRead(readParam) == 0) {
                    boardDao.insertUserRead(readParam);
                    boardDao.updateReadCount(id);
                }
            }
        }
        board.setReplyList(replies);
        return board;
    }

    @Transactional
    @Override
    public void updateBoard(Long id, String username, MultipartHttpServletRequest request) {
        BoardDto board = boardDao.getBoardDetail(id);
        if (board == null || !board.getUsername().equals(username)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        Map<String, Object> param = new HashMap<>();
        param.put("id", id);
        param.put("title", request.getParameter("title"));
        param.put("content", request.getParameter("content"));
        boardDao.updateBoard(param);

        // 기존 파일 삭제 후 새 파일 저장 (파일이 전송된 경우에만)
        List<MultipartFile> newFiles = request.getFiles("files").stream()
                .filter(f -> !f.isEmpty()).toList();
        if (!newFiles.isEmpty()) {
            List<String> oldUrls = boardDao.getBoardFileUrls(id);
            if (!oldUrls.isEmpty()) {
                fileStorageUtil.deleteFiles(BOARD_FOLDER, oldUrls);
                boardDao.deleteBoardFiles(id);
            }
            saveFiles(id, newFiles);
        }
    }

    @Transactional
    @Override
    public void deleteBoard(Long id, String username) {
        BoardDto board = boardDao.getBoardDetail(id);
        if (board == null || !board.getUsername().equals(username)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        List<String> fileUrls = boardDao.getBoardFileUrls(id);
        if (!fileUrls.isEmpty()) {
            fileStorageUtil.deleteFiles(BOARD_FOLDER, fileUrls);
        }
        boardDao.deleteBoardFiles(id);
        boardDao.deleteRepliesByBoard(id);
        boardDao.deleteBoard(id);
    }

    @Transactional(readOnly = true)
    @Override
    public Map<String, Object> getBoardList(BoardParamDto param) {
        List<BoardDto> list = boardDao.getBoardList(param);
        list.forEach(b -> b.setReplyList(boardDao.getReplyList(b.getId())));
        return Map.of("list", list, "count", boardDao.getBoardCount(param));
    }

    @Transactional
    @Override
    public ReplyDto createReply(ReplyRequestDto dto, String username) {
        Map<String, Object> param = new HashMap<>();
        param.put("boardId", dto.getBoardId());
        param.put("username", username);
        param.put("content", dto.getContent());
        boardDao.insertReply(param);

        Long newId = ((Number) param.get("id")).longValue();
        ReplyDto reply = boardDao.getReplyDetail(newId);
        reply.setSameUser(true);
        return reply;
    }

    @Transactional
    @Override
    public void updateReply(Long id, String content, String username) {
        ReplyDto reply = boardDao.getReplyDetail(id);
        if (reply == null || !reply.getUsername().equals(username)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }
        boardDao.updateReply(Map.of("id", id, "content", content));
    }

    @Transactional
    @Override
    public void deleteReply(Long id, String username) {
        ReplyDto reply = boardDao.getReplyDetail(id);
        if (reply == null || !reply.getUsername().equals(username)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }
        boardDao.deleteReply(id);
    }

    private void saveFiles(Long boardId, List<MultipartFile> files) {
        if (files == null || files.isEmpty()) return;
        List<MultipartFile> nonEmpty = files.stream().filter(f -> !f.isEmpty()).toList();
        if (nonEmpty.isEmpty()) return;

        List<String> savedNames = fileStorageUtil.saveFiles(BOARD_FOLDER, nonEmpty);
        for (int i = 0; i < savedNames.size(); i++) {
            MultipartFile file = nonEmpty.get(i);
            Map<String, Object> p = new HashMap<>();
            p.put("boardId",         boardId);
            p.put("fileName",        file.getOriginalFilename());
            p.put("fileSize",        file.getSize());
            p.put("fileContentType", file.getContentType());
            p.put("fileUrl",         savedNames.get(i));
            boardDao.insertBoardFile(p);
        }
    }
}
