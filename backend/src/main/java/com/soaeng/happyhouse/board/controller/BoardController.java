package com.soaeng.happyhouse.board.controller;

import com.soaeng.happyhouse.board.dto.request.BoardParamDto;
import com.soaeng.happyhouse.board.dto.request.ReplyRequestDto;
import com.soaeng.happyhouse.board.dto.response.BoardDto;
import com.soaeng.happyhouse.board.dto.response.ReplyDto;
import com.soaeng.happyhouse.board.service.BoardService;
import com.soaeng.happyhouse.user.entity.UserEntity;
import com.soaeng.happyhouse.util.FileStorageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final FileStorageUtil fileStorageUtil;

    /* ── 게시글 ──────────────────────────────────────────── */

    @GetMapping("")
    public ResponseEntity<Map<String, Object>> getBoardList(BoardParamDto param) {
        return ResponseEntity.ok(boardService.getBoardList(param));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoardDto> getBoard(
            @PathVariable Long id,
            @AuthenticationPrincipal UserEntity user) {
        String username = user != null ? user.getUsername() : null;
        return ResponseEntity.ok(boardService.getBoard(id, username));
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Long> createBoard(
            @AuthenticationPrincipal UserEntity user,
            MultipartHttpServletRequest request) {
        Long id = boardService.createBoard(request, user.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<Void> updateBoard(
            @PathVariable Long id,
            MultipartHttpServletRequest request) {
        boardService.updateBoard(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long id) {
        boardService.deleteBoard(id);
        return ResponseEntity.ok().build();
    }

    /* ── 파일 다운로드 ─────────────────────────────────────── */

    @GetMapping("/files/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        return fileStorageUtil.getFile("board", fileName);
    }

    /* ── 댓글 ──────────────────────────────────────────────── */

    @PostMapping("/reply")
    public ResponseEntity<ReplyDto> createReply(
            @RequestBody ReplyRequestDto dto,
            @AuthenticationPrincipal UserEntity user) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(boardService.createReply(dto, user.getUsername()));
    }

    @PutMapping("/reply/{id}")
    public ResponseEntity<Void> updateReply(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserEntity user) {
        boardService.updateReply(id, body.get("content"), user.getUsername());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/reply/{id}")
    public ResponseEntity<Void> deleteReply(
            @PathVariable Long id,
            @AuthenticationPrincipal UserEntity user) {
        boardService.deleteReply(id, user.getUsername());
        return ResponseEntity.ok().build();
    }
}
