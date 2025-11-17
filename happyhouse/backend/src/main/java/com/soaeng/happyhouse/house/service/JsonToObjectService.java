package com.soaeng.happyhouse.house.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.soaeng.happyhouse.house.entity.SubwayStation;
import com.soaeng.happyhouse.house.repository.SubwayStationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class JsonToObjectService {

    private final SubwayStationRepository repository;

    public void mapSubwayStation(String filePath) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(new File(filePath));
            JsonNode dataArray = root.get("DATA");

            List<SubwayStation> stations = new ArrayList<>();

            for (JsonNode node : dataArray) {
                String bldnId = node.get("bldn_id").asText();

                // 이미 존재하는 데이터는 패스
                if (repository.existsById(bldnId)) {
                    continue;
                }

                SubwayStation station = new SubwayStation();
                station.setBldnId(bldnId);
                station.setBldnNm(node.get("bldn_nm").asText());
                station.setRoute(node.get("route").asText());
                station.setLat(node.get("lat").asText());
                station.setLot(node.get("lot").asText());

                stations.add(station);
            }
            if (!stations.isEmpty()) {
                repository.saveAll(stations);
                log.info("새로운 지하철역 데이터 저장 완료... " + stations.size() + " 건");
            } else {
                log.info("추가된 새로운 데이터가 없습니다.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
