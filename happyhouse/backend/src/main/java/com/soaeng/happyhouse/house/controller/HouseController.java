package com.soaeng.happyhouse.house.controller;

import com.soaeng.happyhouse.house.dto.request.HouseParamDto;
import com.soaeng.happyhouse.house.dto.response.HouseDto;
import com.soaeng.happyhouse.house.dto.response.HouseResponseDto;
import com.soaeng.happyhouse.house.service.HouseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/house")
@RequiredArgsConstructor
public class HouseController {

    private final HouseService service;

    private static final int SUCCESS = 1;
    private static final int FAIL = -1;

    @GetMapping("")
    public ResponseEntity<HouseResponseDto> getHouseList(HouseParamDto param) {
        log.info(param.toString());
        HouseResponseDto response = new HouseResponseDto();

        if (param.getKeyword() != null) {
            if (param.getDongCode() > 0) {
                log.info("[getHouseList] 동, 검색어");
                response.setHouseList(service.getDongKeywordDealList(param));
                Map<String, Object> map = new HashMap<>();
                map.put("dongCode", param.getDongCode());
                map.put("keyword", param.getKeyword());
                response.setCount(service.getDongKeywordDealCount(map));
            } else if (param.getGugunCode() > 0) {
                log.info("[getHouseList] 구군, 검색어");
                response.setHouseList(service.getGugunKeywordDealList(param));
                Map<String, Object> map = new HashMap<>();
                map.put("gugunCode", param.getGugunCode());
                map.put("keyword", param.getKeyword());
                response.setCount(service.getGugunKeywordDealCount(map));
            } else if (param.getSidoCode() > 0) {
                log.info("[getHouseList] 시도, 검색어");
                response.setHouseList(service.getSidoKeywordDealList(param));
                Map<String, Object> map = new HashMap<>();
                map.put("sidoCode", param.getSidoCode());
                map.put("keyword", param.getKeyword());
                response.setCount(service.getSidoKeywordDealCount(map));
            } else {
                log.info("[getHouseList] 검색어");
                response.setHouseList(service.getKeywordDealList(param));
                response.setCount(service.getKeywordDealCount(param.getKeyword()));
            }
        } else {
            if (param.getDongCode() > 0) {
                response.setHouseList(service.getDongDealList(param));
                log.info(response.getHouseList().toString());
                response.setCount(service.getDongDealCount(param.getDongCode()));
            } else if (param.getGugunCode() > 0) {
                response.setHouseList(service.getGugunDealList(param));
                response.setCount(service.getGugunDealCount(param.getGugunCode()));
            } else if (param.getSidoCode() > 0) {
                response.setHouseList(service.getSidoDealList(param));
                response.setCount(service.getSidoDealCount(param.getSidoCode()));
            } else {
                List<HouseDto> houseList = service.getAllDealList(param);
                log.info(service.getAllDealList(param).toString());
                response.setHouseList(houseList);
                log.info(response.getHouseList().toString());
                response.setCount(service.getAllDealCount());
            }
        }

        response.setResult(response.getHouseList() != null ? SUCCESS : FAIL);

        return response.getResult() > 0 ?
                new ResponseEntity<>(response, HttpStatus.OK) :
                new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/{aptCode}")
    public ResponseEntity<HouseResponseDto> getHouseDealList(@PathVariable Integer aptCode) {
        HouseResponseDto response = new HouseResponseDto();

        response.setHouseList(service.getHouseDealList(aptCode));
        response.setCount(service.getHouseDealCount(aptCode));
        response.setResult(response.getHouseList() != null ? SUCCESS : FAIL);

        return response.getResult() > 0 ?
                new ResponseEntity<>(response, HttpStatus.OK) :
                new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
