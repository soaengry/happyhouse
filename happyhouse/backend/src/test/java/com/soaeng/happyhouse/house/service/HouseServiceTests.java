package com.soaeng.happyhouse.house.service;

import com.soaeng.happyhouse.house.dto.request.HouseParamDto;
import com.soaeng.happyhouse.house.dto.response.HouseDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@SpringBootTest
public class HouseServiceTests {

    final long sidoCode = 1100000000;
    final long gugunCode = 1111000000;
    final long dongCode = 1111011500;
    final String keyword = "풍림";
    final int aptCode = 1;

    @Autowired
    private HouseService service;

    @Test
    void contextLoads() {
        assertNotNull(service);
    }

    @Test
    void testGetBaseAddress() {
        log.info(service.getBaseAddress(dongCode).toString());
    }

    @Test
    void testGetSidoList() {
        service.getSidoList().forEach(sido -> log.info(sido.toString()));
    }

    @Test
    void testGetGugunList() {
        service.getGugunList(sidoCode).forEach(gugun -> log.info(gugun.toString()));
    }

    @Test
    void testGetDongList() {
        service.getDongList(gugunCode).forEach(dong -> log.info(dong.toString()));
    }

    @Test
    void testGetAllDealList() {
        HouseParamDto param = new HouseParamDto();
        param.setLimit(10);
        param.setOffset(0);

        List<HouseDto> houseDtoList = service.getAllDealList(param);
        houseDtoList.forEach(houseDto -> log.info(houseDto.toString()));
    }

    @Test
    void testGetAllDealCount() {
        log.info(String.valueOf(service.getAllDealCount()));
    }

    @Test
    void testGetGugunDealList() {
        HouseParamDto param = new HouseParamDto();
        param.setLimit(10);
        param.setOffset(0);
        param.setGugunCode(gugunCode);

        List<HouseDto> houseDtoList = service.getGugunDealList(param);
        houseDtoList.forEach(houseDto -> log.info(houseDto.toString()));
    }

    @Test
    void testGetGugunDealCount() {
        log.info(String.valueOf(service.getGugunDealCount(gugunCode)));
    }

    @Test
    void testGetDongDealList() {
        HouseParamDto param = new HouseParamDto();
        param.setLimit(10);
        param.setOffset(0);
        param.setDongCode(dongCode);

        List<HouseDto> houseDtoList = service.getDongDealList(param);
        houseDtoList.forEach(houseDto -> log.info(houseDto.toString()));
    }

    @Test
    void testGetDongDealCount() {
        log.info(String.valueOf(service.getDongDealCount(gugunCode)));
    }

    @Test
    void testGetKeywordDealList() {
        HouseParamDto param = new HouseParamDto();
        param.setLimit(10);
        param.setOffset(0);
        param.setKeyword(keyword);

        List<HouseDto> houseDtoList = service.getKeywordDealList(param);
        houseDtoList.forEach(houseDto -> log.info(houseDto.toString()));
    }

    @Test
    void testGetKeywordDealCount() {
        log.info(String.valueOf(service.getKeywordDealCount(keyword)));
    }

    @Test
    void testGetSidoKeywordDealList() {
        HouseParamDto param = new HouseParamDto();
        param.setLimit(10);
        param.setOffset(0);
        param.setSidoCode(sidoCode);
        param.setKeyword(keyword);

        List<HouseDto> houseDtoList = service.getSidoKeywordDealList(param);
        houseDtoList.forEach(houseDto -> log.info(houseDto.toString()));
    }

    @Test
    void testGetSidoKeywordDealCount() {
        Map<String, Object> param = new HashMap<>();
        param.put("sidoCode", sidoCode);
        param.put("keyword", keyword);

        log.info(String.valueOf(service.getSidoKeywordDealCount(param)));
    }

    @Test
    void testGetGugunKeywordDealList() {
        HouseParamDto param = new HouseParamDto();
        param.setLimit(10);
        param.setOffset(0);
        param.setGugunCode(gugunCode);
        param.setKeyword(keyword);

        List<HouseDto> houseDtoList = service.getGugunKeywordDealList(param);
        houseDtoList.forEach(houseDto -> log.info(houseDto.toString()));
    }

    @Test
    void testGetGugunKeywordDealCount() {
        Map<String, Object> param = new HashMap<>();
        param.put("gugunCode", gugunCode);
        param.put("keyword", keyword);

        log.info(String.valueOf(service.getGugunKeywordDealCount(param)));
    }

    @Test
    void testGetDongKeywordDealList() {
        HouseParamDto param = new HouseParamDto();
        param.setLimit(10);
        param.setOffset(0);
        param.setDongCode(dongCode);
        param.setKeyword(keyword);

        List<HouseDto> houseDtoList = service.getDongKeywordDealList(param);
        houseDtoList.forEach(houseDto -> log.info(houseDto.toString()));
    }

    @Test
    void testGetDongKeywordDealCount() {
        Map<String, Object> param = new HashMap<>();
        param.put("dongCode", dongCode);
        param.put("keyword", keyword);

        log.info(String.valueOf(service.getDongKeywordDealCount(param)));
    }

    @Test
    void testGetHouseDealList() {
        service.getHouseDealList(aptCode).forEach(deal -> log.info(String.valueOf(deal)));
    }

    @Test
    void testGetHouseDealCount() {
        log.info(String.valueOf(service.getHouseDealCount(aptCode)));
    }

}
