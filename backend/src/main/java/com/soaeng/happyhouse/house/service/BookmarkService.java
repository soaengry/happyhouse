package com.soaeng.happyhouse.house.service;

import com.soaeng.happyhouse.house.dto.response.BaseAddressDto;
import com.soaeng.happyhouse.house.dto.response.HouseResponseDto;
import com.soaeng.happyhouse.user.entity.UserEntity;

import java.util.List;

public interface BookmarkService {

    boolean addBookmarkHouse(UserEntity user, Long aptCode);

    void removeBookmarkHouse(UserEntity user, Long aptCode);

    HouseResponseDto getBookmarkHouseResponse(UserEntity user);

    boolean addBookmarkRegion(UserEntity user, Long dongCode);

    void removeBookmarkRegion(UserEntity user, Long dongCode);

    List<BaseAddressDto> getBookmarkRegionResponse(UserEntity user);
}
