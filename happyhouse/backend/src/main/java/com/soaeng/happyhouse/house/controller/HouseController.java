package com.soaeng.happyhouse.house.controller;

import com.soaeng.happyhouse.external.ApiExplorer;
import com.soaeng.happyhouse.house.dto.request.HouseParamDto;
import com.soaeng.happyhouse.house.dto.response.*;
import com.soaeng.happyhouse.house.repository.SubwayStationRepository;
import com.soaeng.happyhouse.house.service.HouseService;
import com.soaeng.happyhouse.house.service.JsoupCrawler;
import com.soaeng.happyhouse.house.service.SeleniumCrawler;
import com.soaeng.happyhouse.user.entity.UserEntity;
import com.soaeng.happyhouse.util.GeoUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/house")
@RequiredArgsConstructor
public class HouseController {

    private final SubwayStationRepository subwayStationRepository;
    private final HouseService service;
    private final ApiExplorer apiExplorer;
    private final SeleniumCrawler seleniumCrawler;
    private final JsoupCrawler jsoupCrawler;
    private static final int SUCCESS = 1;
    private static final int FAIL = -1;

    @GetMapping("")
    public ResponseEntity<HouseResponseDto> getHouseList(@AuthenticationPrincipal UserEntity user, HouseParamDto param) {
        HouseResponseDto response = new HouseResponseDto();

        if (param.getKeyword() != null) {
            if (param.getDongCode() > 0) {
                log.debug("getDongKeywordHouseList");
                response.setHouseList(service.getDongKeywordHouseList(user, param));
                Map<String, Object> map = new HashMap<>();
                map.put("dongCode", param.getDongCode());
                map.put("keyword", param.getKeyword());
                response.setCount(service.getDongKeywordHouseCount(map));
            } else if (param.getGugunCode() > 0) {
                log.debug("getGugunKeywordHouseList");
                response.setHouseList(service.getGugunKeywordHouseList(user, param));
                Map<String, Object> map = new HashMap<>();
                map.put("gugunCode", param.getGugunCode());
                map.put("keyword", param.getKeyword());
                response.setCount(service.getGugunKeywordHouseCount(map));
            } else if (param.getSidoCode() > 0) {
                log.debug("getSidoKeywordHouseList");
                response.setHouseList(service.getSidoKeywordHouseList(user, param));
                Map<String, Object> map = new HashMap<>();
                map.put("sidoCode", param.getSidoCode());
                map.put("keyword", param.getKeyword());
                response.setCount(service.getSidoKeywordHouseCount(map));
            } else {
                log.debug("getKeywordHouseList");
                response.setHouseList(service.getKeywordHouseList(user, param));
                response.setCount(service.getKeywordHouseCount(param.getKeyword()));
            }
        } else {
            if (param.getDongCode() > 0) {
                log.debug("getDongHouseList");
                response.setHouseList(service.getDongHouseList(user, param));
                response.setCount(service.getDongHouseCount(param.getDongCode()));
            } else if (param.getGugunCode() > 0) {
                log.debug("getGugunHouseList");
                response.setHouseList(service.getGugunHouseList(user, param));
                response.setCount(service.getGugunHouseCount(param.getGugunCode()));
            } else if (param.getSidoCode() > 0) {
                log.debug("getSidoHouseList");
                response.setHouseList(service.getSidoHouseList(user, param));
                response.setCount(service.getSidoHouseCount(param.getSidoCode()));
            } else {
                log.debug("getAllHouseList");
                List<HouseDto> houseList = service.getAllHouseList(user, param);
                response.setHouseList(houseList);
                response.setCount(service.getAllHouseCount());
            }
        }

        response.setResult(response.getHouseList() != null ? SUCCESS : FAIL);
        return response.getResult() > 0 ?
                new ResponseEntity<>(response, HttpStatus.OK) :
                new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/{aptCode}")
    public ResponseEntity<HouseResponseDto> getHouseDealList(@PathVariable Long aptCode) {
        HouseResponseDto response = new HouseResponseDto();

        response.setHouseList(service.getHouseDealList(aptCode));
        response.setCount(service.getHouseDealCount(aptCode));
        response.setResult(response.getHouseList() != null ? SUCCESS : FAIL);

        return response.getResult() > 0 ?
                new ResponseEntity<>(response, HttpStatus.OK) :
                new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/busStops")
    public ResponseEntity<List<BusStopItem>> getNearbyBusStops(@RequestParam String lat, @RequestParam String lng) {
        List<BusStopItem> response = apiExplorer.getNearbyBusStops(lat, lng);
        log.info(response.toString());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/subwayStations")
    public ResponseEntity<List<SubwayStationDto>> getNearbySubwayStations(
            @RequestParam String lat,
            @RequestParam String lng) {

        // 반경 1km 이내 필터링 - 거리순 정렬
        List<SubwayStationDto> nearbyStations = subwayStationRepository.findAll().stream()
                .map(st -> {
                    double distance = GeoUtil.distance(Double.parseDouble(lat), Double.parseDouble(lng), Double.parseDouble(st.getLat()), Double.parseDouble(st.getLot()));
                    return new SubwayStationDto(st.getBldnNm(), st.getRoute(), distance);
                })
                .filter(dto -> dto.getDistance() <= 1000)
                .sorted(Comparator.comparingDouble(SubwayStationDto::getDistance))
                .collect(Collectors.toList());

        return ResponseEntity.ok(nearbyStations);
    }

    @GetMapping("/population/{dongCode}")
    public ResponseEntity<PopulationDto> getPopulation(@PathVariable Long dongCode) {
        PopulationDto dto = service.getPopulation(dongCode);
        log.info(dto.toString());

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/news")
    public ResponseEntity<List<NewsDto>> getNews(@RequestParam Long dongCode) {
        List<NewsDto> newsDtoList = jsoupCrawler.crawlNews(dongCode);

        return newsDtoList != null ?
                new ResponseEntity<>(newsDtoList, HttpStatus.OK) :
                new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);

    }

}
