package com.soaeng.happyhouse.board.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "board_file")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "board_id", nullable = false)
    private Long boardId;

    @Column(name = "file_name", length = 500)
    private String fileName;

    private Long fileSize;

    @Column(name = "file_content_type", length = 200)
    private String fileContentType;

    @Column(name = "file_url", length = 500)
    private String fileUrl;

    @CreatedDate
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;
}
