package com.soaeng.happyhouse.house.entity;

import com.soaeng.happyhouse.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "bookmark_region", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "dong_code"})
})
public class BookmarkRegion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "dong_code")
    private Long dongCode;

    @Builder
    public BookmarkRegion(UserEntity user, Long dongCode) {
        this.user = user;
        this.dongCode = dongCode;
    }
}
