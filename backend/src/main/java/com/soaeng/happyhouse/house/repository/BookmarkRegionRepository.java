package com.soaeng.happyhouse.house.repository;

import com.soaeng.happyhouse.house.entity.BookmarkRegion;
import com.soaeng.happyhouse.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookmarkRegionRepository extends JpaRepository<BookmarkRegion, Long> {

    List<BookmarkRegion> findByUser(UserEntity user);

    Optional<BookmarkRegion> findByUserAndDongCode(UserEntity user, Long dongCode);
}
