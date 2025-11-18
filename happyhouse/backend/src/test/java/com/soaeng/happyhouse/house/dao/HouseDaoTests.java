package com.soaeng.happyhouse.house.dao;

import com.soaeng.happyhouse.house.dto.request.HouseParamDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@SpringBootTest
public class HouseDaoTests {

    final long sidoCode = 1100000000;
    final long gugunCode = 1111000000;
    final long dongCode = 1111011500;
    final String dongName = "사직동";
    final String keyword = "풍림";
    final long aptCode = 1;

    @Autowired
    private HouseDao dao;

    @Test
    void contextLoads() {
        assertNotNull(dao);
    }

    @Test
    void testGetBaseAddress() {
        log.info(dao.getBaseAddress(dongCode).toString());
    }

    @Test
    void testGetSidoList() {
        dao.getSidoList().forEach(sido -> log.info(String.valueOf(sido)));
    }

    @Test
    void testGetGugunList() {
        dao.getGugunList(sidoCode).forEach(gugun -> log.info(String.valueOf(gugun)));
    }

    @Test
    void testGetDongList() {
        dao.getDongList(gugunCode).forEach(dong -> log.info(String.valueOf(dong)));
    }

    @Test
    void testGetAllDealList() {
        HouseParamDto dto = new HouseParamDto();
        dto.setLimit(10);
        dto.setOffset(0);
        dto.setDongCode(dongCode);
        log.info(dao.getAllHouseList(dto).toString());
    }

    @Test
    void testGetAllDealCount() {
        log.info(String.valueOf(dao.getAllHouseCount()));
    }

    @Test
    void testGetGugunDealList() {
        HouseParamDto dto = new HouseParamDto();
        dto.setLimit(10);
        dto.setOffset(0);
        dto.setGugunCode(gugunCode);
        log.info(dao.getGugunHouseList(dto).toString());
    }

    @Test
    void testGetGugunDealCount() {
        log.info(String.valueOf(dao.getGugunHouseCount(gugunCode)));
    }

    @Test
    void testGetDongDealList() {
        HouseParamDto dto = new HouseParamDto();
        dto.setLimit(10);
        dto.setOffset(0);
        dto.setDongCode(dongCode);
        dao.getDongHouseList(dto).forEach(dongDeal -> log.info(String.valueOf(dongDeal)));
    }

    @Test
    void testGetDongDealCount() {
        log.info(String.valueOf(dao.getDongHouseCount(dongCode)));
    }

    @Test
    void testGetKeywordDealList() {
        HouseParamDto dto = new HouseParamDto();
        dto.setLimit(10);
        dto.setOffset(0);
        dto.setKeyword(keyword);
        dao.getKeywordHouseList(dto).forEach(deal -> log.info(String.valueOf(deal)));
    }

    @Test
    void testGetKeywordDealCount() {
        log.info(String.valueOf(dao.getKeywordHouseCount(keyword)));
    }

    @Test
    void testGetSidoKeywordDealList() {
        HouseParamDto dto = new HouseParamDto();
        dto.setLimit(10);
        dto.setOffset(0);
        dto.setSidoCode(sidoCode);
        dto.setKeyword(keyword);
        dao.getSidoKeywordHouseList(dto).forEach(deal -> log.info(deal.toString()));
    }

    @Test
    void testGetSidoKeywordDealCount() {
        Map<String, Object> map = new HashMap<>();
        map.put("sidoCode", sidoCode);
        map.put("keyword", keyword);
        log.info(String.valueOf(dao.getSidoKeywordHouseCount(map)));
    }

    @Test
    void testGetGugunKeywordDealList() {
        HouseParamDto dto = new HouseParamDto();
        dto.setLimit(10);
        dto.setOffset(0);
        dto.setGugunCode(gugunCode);
        dto.setKeyword(keyword);
        dao.getGugunKeywordHouseList(dto).forEach(deal -> log.info(deal.toString()));
    }

    @Test
    void testGetGugunKeywordDealCount() {
        Map<String, Object> map = new HashMap<>();
        map.put("gugunCode", gugunCode);
        map.put("keyword", keyword);
        log.info(String.valueOf(dao.getGugunKeywordHouseCount(map)));
    }

    @Test
    void testGetDongKeywordDealList() {
        HouseParamDto dto = new HouseParamDto();
        dto.setLimit(10);
        dto.setOffset(0);
        dto.setDongCode(dongCode);
        dto.setKeyword(keyword);
        dao.getDongKeywordHouseList(dto).forEach(deal -> log.info(deal.toString()));
    }

    @Test
    void testGetDongKeywordDealCount() {
        Map<String, Object> map = new HashMap<>();
        map.put("dongCode", dongCode);
        map.put("keyword", keyword);
        log.info(String.valueOf(dao.getDongKeywordHouseCount(map)));
    }

    @Test
    void testGetHouseDealList() {
        dao.getHouseDealList(aptCode).forEach(deal -> log.info(deal.toString()));
    }

    @Test
    void testGetHouseDealCount() {
        log.info(String.valueOf(dao.getHouseDealCount(aptCode)));
    }

}
