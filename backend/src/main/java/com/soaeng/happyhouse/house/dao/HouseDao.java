package com.soaeng.happyhouse.house.dao;

import com.soaeng.happyhouse.house.dto.request.HouseParamDto;
import com.soaeng.happyhouse.house.dto.response.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface HouseDao {

    /* 지역 정보 */
    BaseAddressDto getBaseAddress(Long dongCode);

    List<BaseAddressDto> getBaseAddressByDongCodes(List<Long> dongCodes);

    List<SidoDto> getSidoList();

    List<GugunDto> getGugunList(Long sidoCode);

    List<DongDto> getDongList(Long gugunCode);

    String getAdstrdCode(Long dongCode);

    Long getGugunCode(Long dongCode);

    /* 매물별 최근 거래 목록 (동적 필터 통합) */
    List<HouseDto> getHouseList(HouseParamDto param);

    int getHouseCount(HouseParamDto param);

    /* 거래 상세 */
    List<HouseDto> getHouseDealList(Long aptCode);

    int getHouseDealCount(Long aptCode);

    /* 북마크 */
    List<HouseDto> getBookmarkHouseList(List<Long> aptCodes);

    List<BaseAddressDto> getBookmarkRegionList(List<Long> dongCodes);

    /* 인구 통계 */
    PopulationDto getPopulation(String adstrdCode);
}
