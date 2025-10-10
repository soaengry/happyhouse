package com.soaeng.happyhouse.house.dao;

import com.soaeng.happyhouse.house.dto.BaseAddressDto;
import com.soaeng.happyhouse.house.dto.DongDto;
import com.soaeng.happyhouse.house.dto.GugunDto;
import com.soaeng.happyhouse.house.dto.SidoDto;
import com.soaeng.happyhouse.house.dto.request.HouseParamDto;
import com.soaeng.happyhouse.house.dto.response.HouseDto;
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
    List<HouseDto> getAllDealList(HouseParamDto param);

    int getAllDealCount();

    // 시/도
    List<HouseDto> getSidoDealList(HouseParamDto param);

    int getSidoDealCount(Long sidoCode);

    // 구/군
    List<HouseDto> getGugunDealList(HouseParamDto param);

    int getGugunDealCount(Long gugunCode);

    // 동
    List<HouseDto> getDongDealList(HouseParamDto param);

    int getDongDealCount(Long dongCode);

    // 매물명 검색
    List<HouseDto> getKeywordDealList(HouseParamDto param);

    int getKeywordDealCount(String keyword);

    // 시 매물명 검색
    List<HouseDto> getSidoKeywordDealList(HouseParamDto param);

    int getSidoKeywordDealCount(Map<String, Object> param);

    // 구/군 매물명 검색
    List<HouseDto> getGugunKeywordDealList(HouseParamDto param);

    int getGugunKeywordDealCount(Map<String, Object> param);

    // 동별 매물명 검색
    List<HouseDto> getDongKeywordDealList(HouseParamDto param);

    int getDongKeywordDealCount(Map<String, Object> param);


    /* 거래 상세 */
    // 매물별 거래 상세 목록
    List<HouseDto> getHouseDealList(Integer aptCode);

    int getHouseDealCount(Integer aptCode);

}
