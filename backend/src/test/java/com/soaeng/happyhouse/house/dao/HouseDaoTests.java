package com.soaeng.happyhouse.house.dao;

import com.soaeng.happyhouse.house.dto.request.HouseParamDto;
import com.soaeng.happyhouse.house.dto.response.HouseDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
public class HouseDaoTests {

    static final long SIDO_CODE = 1100000000L;
    static final long GUGUN_CODE = 1111000000L;
    static final long DONG_CODE = 1111011500L;
    static final String KEYWORD = "풍림";
    static final long APT_CODE = 1L;

    @Autowired
    HouseDao dao;

    @Test
    void contextLoads() {
        assertThat(dao).isNotNull();
    }

    @Test
    void testGetBaseAddress() {
        var result = dao.getBaseAddress(DONG_CODE);
        assertThat(result).isNotNull();
        log.info("{}", result);
    }

    @Test
    void testGetSidoList() {
        var result = dao.getSidoList();
        assertThat(result).isNotEmpty();
    }

    @Test
    void testGetGugunList() {
        var result = dao.getGugunList(SIDO_CODE);
        assertThat(result).isNotEmpty();
    }

    @Test
    void testGetDongList() {
        var result = dao.getDongList(GUGUN_CODE);
        assertThat(result).isNotEmpty();
    }

    @Test
    void testGetHouseListAll() {
        HouseParamDto param = buildParam(0, 0, 0, null);
        List<HouseDto> result = dao.getHouseList(param);
        assertThat(result).isNotEmpty();
        log.info("전체 목록 count={}", result.size());
    }

    @Test
    void testGetHouseListBySido() {
        HouseParamDto param = buildParam(SIDO_CODE, 0, 0, null);
        List<HouseDto> result = dao.getHouseList(param);
        assertThat(result).isNotEmpty();
        log.info("시도 목록 count={}", result.size());
    }

    @Test
    void testGetHouseListByGugun() {
        HouseParamDto param = buildParam(0, GUGUN_CODE, 0, null);
        List<HouseDto> result = dao.getHouseList(param);
        assertThat(result).isNotEmpty();
        log.info("구군 목록 count={}", result.size());
    }

    @Test
    void testGetHouseListByDong() {
        HouseParamDto param = buildParam(0, 0, DONG_CODE, null);
        List<HouseDto> result = dao.getHouseList(param);
        assertThat(result).isNotEmpty();
        log.info("동 목록 count={}", result.size());
    }

    @Test
    void testGetHouseListByKeyword() {
        HouseParamDto param = buildParam(0, 0, 0, KEYWORD);
        List<HouseDto> result = dao.getHouseList(param);
        assertThat(result).allSatisfy(dto -> assertThat(dto.getAptName()).contains(KEYWORD));
        log.info("키워드 목록 count={}", result.size());
    }

    @Test
    void testGetHouseListBySidoKeyword() {
        HouseParamDto param = buildParam(SIDO_CODE, 0, 0, KEYWORD);
        List<HouseDto> result = dao.getHouseList(param);
        log.info("시도+키워드 목록 count={}", result.size());
    }

    @Test
    void testGetHouseCount() {
        HouseParamDto param = buildParam(0, GUGUN_CODE, 0, null);
        int count = dao.getHouseCount(param);
        assertThat(count).isGreaterThanOrEqualTo(0);
        log.info("구군 총 수={}", count);
    }

    @Test
    void testGetHouseDealList() {
        List<HouseDto> result = dao.getHouseDealList(APT_CODE);
        log.info("거래 내역 count={}", result.size());
    }

    @Test
    void testGetHouseDealCount() {
        int count = dao.getHouseDealCount(APT_CODE);
        assertThat(count).isGreaterThanOrEqualTo(0);
    }

    private HouseParamDto buildParam(long sidoCode, long gugunCode, long dongCode, String keyword) {
        HouseParamDto param = new HouseParamDto();
        param.setLimit(10);
        param.setOffset(0);
        param.setSidoCode(sidoCode);
        param.setGugunCode(gugunCode);
        param.setDongCode(dongCode);
        param.setKeyword(keyword);
        return param;
    }
}
