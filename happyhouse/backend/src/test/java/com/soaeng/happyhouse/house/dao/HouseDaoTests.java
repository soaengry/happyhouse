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
    final int aptCode = 1;

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
        log.info(dao.getAllDealList(dto).toString());
    }

    @Test
    void testGetAllDealCount() {
        log.info(String.valueOf(dao.getAllDealCount()));
    }

    @Test
    void testGetGugunDealList() {
        HouseParamDto dto = new HouseParamDto();
        dto.setLimit(10);
        dto.setOffset(0);
        dto.setGugunCode(gugunCode);
        log.info(dao.getGugunDealList(dto).toString());
    }

    @Test
    void testGetGugunDealCount() {
        log.info(String.valueOf(dao.getGugunDealCount(gugunCode)));
    }

    @Test
    void testGetDongDealList() {
        HouseParamDto dto = new HouseParamDto();
        dto.setLimit(10);
        dto.setOffset(0);
        dto.setDongCode(dongCode);
        dao.getDongDealList(dto).forEach(dongDeal -> log.info(String.valueOf(dongDeal)));
    }

    @Test
    void testGetDongDealCount() {
        log.info(String.valueOf(dao.getDongDealCount(dongCode)));
    }

    @Test
    void testGetKeywordDealList() {
        HouseParamDto dto = new HouseParamDto();
        dto.setLimit(10);
        dto.setOffset(0);
        dto.setKeyword(keyword);
        dao.getKeywordDealList(dto).forEach(deal -> log.info(String.valueOf(deal)));
    }

    @Test
    void testGetKeywordDealCount() {
        log.info(String.valueOf(dao.getKeywordDealCount(keyword)));
    }

    @Test
    void testGetSidoKeywordDealList() {
        HouseParamDto dto = new HouseParamDto();
        dto.setLimit(10);
        dto.setOffset(0);
        dto.setSidoCode(sidoCode);
        dto.setKeyword(keyword);
        dao.getSidoKeywordDealList(dto).forEach(deal -> log.info(deal.toString()));
    }

    @Test
    void testGetSidoKeywordDealCount() {
        Map<String, Object> map = new HashMap<>();
        map.put("sidoCode", sidoCode);
        map.put("keyword", keyword);
        log.info(String.valueOf(dao.getSidoKeywordDealCount(map)));
    }
    @Test
    void testGetGugunKeywordDealList() {
        HouseParamDto dto = new HouseParamDto();
        dto.setLimit(10);
        dto.setOffset(0);
        dto.setGugunCode(gugunCode);
        dto.setKeyword(keyword);
        dao.getGugunKeywordDealList(dto).forEach(deal -> log.info(deal.toString()));
    }

    @Test
    void testGetGugunKeywordDealCount() {
        Map<String, Object> map = new HashMap<>();
        map.put("gugunCode", gugunCode);
        map.put("keyword", keyword);
        log.info(String.valueOf(dao.getGugunKeywordDealCount(map)));
    }

    @Test
    void testGetDongKeywordDealList() {
        HouseParamDto dto = new HouseParamDto();
        dto.setLimit(10);
        dto.setOffset(0);
        dto.setDongCode(dongCode);
        dto.setKeyword(keyword);
        dao.getDongKeywordDealList(dto).forEach(deal -> log.info(deal.toString()));
    }

    @Test
    void testGetDongKeywordDealCount() {
        Map<String, Object> map = new HashMap<>();
        map.put("dongCode", dongCode);
        map.put("keyword", keyword);
        log.info(String.valueOf(dao.getDongKeywordDealCount(map)));
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
