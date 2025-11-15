package com.soaeng.happyhouse.house.dao;

import com.soaeng.happyhouse.house.dto.request.HouseParamDto;
import com.soaeng.happyhouse.house.dto.response.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface HouseDao {

    /* 지역 정보 */
    // 법정동
    BaseAddressDto getBaseAddress(Long dongCode);

    // 시/도
    List<SidoDto> getSidoList();

    // 구/군
    List<GugunDto> getGugunList(Long sidoCode);

    // 동
    List<DongDto> getDongList(Long gugunCode);


    /* 매물별 최근 거래 목록 */
    // 전체
    List<HouseDto> getAllHouseList(HouseParamDto param);

    int getAllHouseCount();

    // 시/도
    List<HouseDto> getSidoDealList(HouseParamDto param);

    int getSidoHouseCount(Long sidoCode);

    // 구/군
    List<HouseDto> getGugunHouseList(HouseParamDto param);

    int getGugunHouseCount(Long gugunCode);

    // 동
    List<HouseDto> getDongHouseList(HouseParamDto param);

    int getDongHouseCount(Long dongCode);

    // 매물명 검색
    List<HouseDto> getKeywordHouseList(HouseParamDto param);

    int getKeywordHouseCount(String keyword);

    // 시 매물명 검색
    List<HouseDto> getSidoKeywordHouseList(HouseParamDto param);

    int getSidoKeywordHouseCount(Map<String, Object> param);

    // 구/군 매물명 검색
    List<HouseDto> getGugunKeywordHouseList(HouseParamDto param);

    int getGugunKeywordHouseCount(Map<String, Object> param);

    // 동별 매물명 검색
    List<HouseDto> getDongKeywordHouseList(HouseParamDto param);

    int getDongKeywordHouseCount(Map<String, Object> param);


    /* 거래 상세 */
    // 매물별 거래 상세 목록
    List<HouseDto> getHouseDealList(Integer aptCode);

    int getHouseDealCount(Integer aptCode);

    List<HouseDto> getBookmarkHouseList(List<Long> aptCodes);

}
