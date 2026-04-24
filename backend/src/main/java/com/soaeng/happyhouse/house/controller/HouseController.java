package com.soaeng.happyhouse.house.controller;

import com.soaeng.happyhouse.external.ApiExplorer;
import com.soaeng.happyhouse.house.dto.request.HouseParamDto;
import com.soaeng.happyhouse.house.dto.response.*;
import com.soaeng.happyhouse.house.service.HouseService;
import com.soaeng.happyhouse.house.service.JsoupCrawler;
import com.soaeng.happyhouse.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/house")
@RequiredArgsConstructor
public class HouseController {

    private final HouseService service;
    private final ApiExplorer apiExplorer;
    private final JsoupCrawler jsoupCrawler;

    @GetMapping("")
    public ResponseEntity<HouseResponseDto> getHouseList(
            @AuthenticationPrincipal UserEntity user,
            HouseParamDto param) {

        List<HouseDto> houseList = service.getHouseList(user, param);
        int count = service.getHouseCount(param);

        HouseResponseDto response = new HouseResponseDto();
        response.setHouseList(houseList);
        response.setCount(count);
        response.setResult(1);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{aptCode}")
    public ResponseEntity<HouseResponseDto> getHouseDealList(@PathVariable Long aptCode) {
        HouseResponseDto response = new HouseResponseDto();
        response.setHouseList(service.getHouseDealList(aptCode));
        response.setCount(service.getHouseDealCount(aptCode));
        response.setResult(1);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/busStops")
    public ResponseEntity<List<BusStopItem>> getNearbyBusStops(
            @RequestParam String lat,
            @RequestParam String lng) {
        return ResponseEntity.ok(apiExplorer.getNearbyBusStops(lat, lng));
    }

    @GetMapping("/subwayStations")
    public ResponseEntity<List<SubwayStationDto>> getNearbySubwayStations(
            @RequestParam double lat,
            @RequestParam double lng) {
        return ResponseEntity.ok(service.getNearbySubwayStations(lat, lng));
    }

    @GetMapping("/population/{dongCode}")
    public ResponseEntity<PopulationDto> getPopulation(@PathVariable Long dongCode) {
        PopulationDto dto = service.getPopulation(dongCode);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @GetMapping("/news")
    public ResponseEntity<List<NewsDto>> getNews(@RequestParam Long dongCode) {
        List<NewsDto> newsDtoList = jsoupCrawler.crawlNews(dongCode);
        return newsDtoList != null
                ? ResponseEntity.ok(newsDtoList)
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
