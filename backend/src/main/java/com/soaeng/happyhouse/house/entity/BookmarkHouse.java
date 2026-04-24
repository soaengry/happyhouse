package com.soaeng.happyhouse.house.entity;

import com.soaeng.happyhouse.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "bookmark_house", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "apt_code"})
})
public class BookmarkHouse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "apt_code")
    private Long aptCode;

    @Builder
    public BookmarkHouse(UserEntity user, Long aptCode) {
        this.user = user;
        this.aptCode = aptCode;
    }
}
