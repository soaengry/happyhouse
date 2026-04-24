package com.soaeng.happyhouse.house.repository;

import com.soaeng.happyhouse.house.entity.BookmarkHouse;
import com.soaeng.happyhouse.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookmarkHouseRepository extends JpaRepository<BookmarkHouse, Long> {

    List<BookmarkHouse> findByUser(UserEntity user);

    Optional<BookmarkHouse> findByUserAndAptCode(UserEntity user, Long aptCode);
}
