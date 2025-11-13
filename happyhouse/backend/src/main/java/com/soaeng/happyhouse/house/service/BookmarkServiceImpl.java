package com.soaeng.happyhouse.house.service;

import com.soaeng.happyhouse.house.dao.HouseDao;
import com.soaeng.happyhouse.house.dto.response.HouseDto;
import com.soaeng.happyhouse.house.dto.response.HouseResponseDto;
import com.soaeng.happyhouse.house.entity.BookmarkHouse;
import com.soaeng.happyhouse.house.repository.BookmarkHouseRepository;
import com.soaeng.happyhouse.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookmarkServiceImpl {

    private final BookmarkHouseRepository bookmarkHouseRepository;
    private final HouseDao houseDao;

    @Transactional
    public boolean addBookmarkHouse(UserEntity user, Long aptCode) {
        if (bookmarkHouseRepository.findByUserAndAptCode(user, aptCode).isEmpty()) {
            BookmarkHouse bookmark = BookmarkHouse
                    .builder()
                    .user(user)
                    .aptCode(aptCode).build();
            return bookmarkHouseRepository.save(bookmark).getId() > 0;
        }
        return false;
    }

    @Transactional
    public void removeBookmarkHouse(UserEntity user, Long aptCode) {
        bookmarkHouseRepository.findByUserAndAptCode(user, aptCode)
                .ifPresent(bookmarkHouseRepository::delete);
    }

    public HouseResponseDto getBookmarkHouseResponse(UserEntity user) {
        // 1. 유저의 북마크 조회
        List<BookmarkHouse> bookmarks = bookmarkHouseRepository.findByUser(user);

        // 2. aptCode 목록 추출
        List<Long> aptCodes = bookmarks.stream()
                .map(BookmarkHouse::getAptCode)
                .collect(Collectors.toList());

        if (aptCodes.isEmpty()) {
            return new HouseResponseDto(); // 빈 응답
        }

        // 3. MyBatis 쿼리로 HouseDto 조회
        List<HouseDto> houseList = houseDao.getBookmarkHouseList(aptCodes);

        // 4. 응답 DTO 구성
        HouseResponseDto response = new HouseResponseDto();
        response.setResult(1);
        response.setHouseList(houseList);
        response.setCount(houseList.size());

        return response;
    }

}
