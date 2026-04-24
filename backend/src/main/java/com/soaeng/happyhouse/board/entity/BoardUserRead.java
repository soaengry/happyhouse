package com.soaeng.happyhouse.board.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "board_user_read")
@IdClass(BoardUserRead.BoardUserReadId.class)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardUserRead {

    @Id
    @Column(name = "board_id")
    private Long boardId;

    @Id
    @Column(nullable = false)
    private String username;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BoardUserReadId implements Serializable {
        private Long boardId;
        private String username;
    }
}
