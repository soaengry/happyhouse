package com.soaeng.happyhouse.house.service;

import com.soaeng.happyhouse.house.dto.request.HouseParamDto;
import com.soaeng.happyhouse.house.dto.response.*;
import com.soaeng.happyhouse.user.entity.UserEntity;

import java.util.List;

public interface HouseService {

    /* 지역 정보 */
    BaseAddressDto getBaseAddress(Long dongCode);

    List<SidoDto> getSidoList();

    List<GugunDto> getGugunList(Long sidoCode);

    List<DongDto> getDongList(Long gugunCode);

    /* 매물 검색 (동적 필터: sido/gugun/dong/keyword 조합) */
    List<HouseDto> getHouseList(UserEntity user, HouseParamDto param);

    int getHouseCount(HouseParamDto param);

    /* 거래 상세 */
    List<HouseDto> getHouseDealList(Long aptCode);

    int getHouseDealCount(Long aptCode);

    /* 주변 정보 */
    List<SubwayStationDto> getNearbySubwayStations(double lat, double lng);

    PopulationDto getPopulation(Long dongCode);
}
