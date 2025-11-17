//package com.soaeng.happyhouse.util;
//
//import com.soaeng.happyhouse.house.service.JsonToObjectService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import java.io.File;
//
//@Component
//@RequiredArgsConstructor
//// 서울 지하철역 json데이터를 db에 저장 - 애플리케이션 실행 할 때 동작
//public class SubwayStationImporterRunner implements CommandLineRunner {
//
//    private final JsonToObjectService service;
//
//    @Override
//    public void run(String... args) {
//        service.mapSubwayStation(new File("data" + File.separator + "seoul_subway_station.json").getAbsolutePath());
//    }
//}
