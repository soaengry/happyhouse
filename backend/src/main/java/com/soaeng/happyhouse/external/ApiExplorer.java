package com.soaeng.happyhouse.external;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soaeng.happyhouse.house.dto.response.BusStopItem;
import com.soaeng.happyhouse.house.dto.response.PopulationDto;
import com.soaeng.happyhouse.house.repository.PopulationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApiExplorer {

    @Value("${key.data-go-kr}")
    private String SERVICE_KEY_DATA;
    @Value("${key.data-seoul-go-kr}")
    private String SERVICE_KEY_DATA_SEOUL;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final PopulationRepository populationRepository;

    public List<BusStopItem> getNearbyBusStops(String gpsLati, String gpsLong) {
        try {
            StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1613000/BusSttnInfoInqireService/getCrdntPrxmtSttnList"); /*URL*/
            urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + SERVICE_KEY_DATA); /*Service Key*/
            urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
            urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("10", "UTF-8")); /*한 페이지 결과 수*/
            urlBuilder.append("&" + URLEncoder.encode("_type", "UTF-8") + "=" + URLEncoder.encode("json", "UTF-8")); /*데이터 타입(xml, json)*/
            urlBuilder.append("&" + URLEncoder.encode("gpsLati", "UTF-8") + "=" + URLEncoder.encode(gpsLati, "UTF-8")); /*WGS84 위도 좌표*/
            urlBuilder.append("&" + URLEncoder.encode("gpsLong", "UTF-8") + "=" + URLEncoder.encode(gpsLong, "UTF-8")); /*WGS84 경도 좌표*/

            log.info(urlBuilder.toString());
            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");

            log.info("Response code: " + conn.getResponseCode());

            BufferedReader rd;
            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();
            conn.disconnect();

            Map<String, Object> map = objectMapper.readValue(sb.toString(), Map.class);
            Map<String, Object> responseMap = (Map<String, Object>) map.get("response");
            Map<String, Object> bodyMap = (Map<String, Object>) responseMap.get("body");
            Object itemsObj = bodyMap.get("items");

            List<BusStopItem> busStopItems = new ArrayList<>();
            if (itemsObj instanceof Map) {
                Map<String, Object> itemsMap = (Map<String, Object>) itemsObj;
                Object itemObj = itemsMap.get("item");
                if (itemObj instanceof List) {
                    busStopItems = objectMapper.convertValue(itemObj,
                            objectMapper.getTypeFactory().constructCollectionType(List.class, BusStopItem.class));
                }
            } else {
                busStopItems = new ArrayList<>();
            }
            return busStopItems;

        } catch (Exception exception) {
            log.error(exception.getMessage());
            return null;
        }
    }

    public PopulationDto getPopulation(String adstrdCode) {
        try {
            StringBuilder urlBuilder = new StringBuilder("http://openapi.seoul.go.kr:8088"); /*URL*/
            urlBuilder.append("/" + URLEncoder.encode(SERVICE_KEY_DATA_SEOUL, "UTF-8")); /*인증키 (sample사용시에는 호출시 제한됩니다.)*/
            urlBuilder.append("/" + URLEncoder.encode("json", "UTF-8")); /*요청파일타입 (xml,xmlf,xls,json) */
            urlBuilder.append("/" + URLEncoder.encode("SPOP_LOCAL_RESD_DONG", "UTF-8")); /*서비스명 (대소문자 구분 필수입니다.)*/
            urlBuilder.append("/" + URLEncoder.encode("1", "UTF-8")); /*요청시작위치 (sample인증키 사용시 5이내 숫자)*/
            urlBuilder.append("/" + URLEncoder.encode("5", "UTF-8")); /*요청종료위치(sample인증키 사용시 5이상 숫자 선택 안 됨)*/
            // 상위 5개는 필수적으로 순서바꾸지 않고 호출해야 합니다.

            // 한달 전 날짜
            LocalDate today = LocalDate.now();
            LocalDate oneWeekAgo = today.minusWeeks(1);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            String formattedDate = oneWeekAgo.format(formatter);


            // 서비스별 추가 요청 인자이며 자세한 내용은 각 서비스별 '요청인자'부분에 자세히 나와 있습니다.
            urlBuilder.append("/" + URLEncoder.encode(formattedDate, "UTF-8")); // 기준일
            urlBuilder.append("/" + URLEncoder.encode("00", "UTF-8")); // 시간대구분
            urlBuilder.append("/" + URLEncoder.encode(adstrdCode, "UTF-8")); // 행정동코드

            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/xml");
            log.info("Response code: " + conn.getResponseCode()); /* 연결 자체에 대한 확인이 필요하므로 추가합니다.*/
            BufferedReader rd;

            // 서비스코드가 정상이면 200~300사이의 숫자가 나옵니다.
            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();
            conn.disconnect();

            Map<String, Object> map = objectMapper.readValue(sb.toString(), Map.class);
            Map<String, Object> responseMap = (Map<String, Object>) map.get("SPOP_LOCAL_RESD_DONG");
            List<Map<String, Object>> rows = (List<Map<String, Object>>) responseMap.get("row");
            Map<String, Object> row = rows.get(0); // 첫 번째 행정동 데이터

            PopulationDto populationDto = PopulationDto.builder()
                    .adstrdCode((String) row.get("ADSTRD_CODE_SE"))
                    .totalLocal(Double.parseDouble((String) row.get("TOT_LVPOP_CO")))
                    .maleTo19(
                            Double.parseDouble((String) row.get("MALE_F0T9_LVPOP_CO")) +
                                    Double.parseDouble((String) row.get("MALE_F10T14_LVPOP_CO")) +
                                    Double.parseDouble((String) row.get("MALE_F15T19_LVPOP_CO"))
                    )
                    .maleTo39(
                            Double.parseDouble((String) row.get("MALE_F20T24_LVPOP_CO")) +
                                    Double.parseDouble((String) row.get("MALE_F25T29_LVPOP_CO")) +
                                    Double.parseDouble((String) row.get("MALE_F30T34_LVPOP_CO")) +
                                    Double.parseDouble((String) row.get("MALE_F35T39_LVPOP_CO"))
                    )
                    .maleTo59(
                            Double.parseDouble((String) row.get("MALE_F40T44_LVPOP_CO")) +
                                    Double.parseDouble((String) row.get("MALE_F45T49_LVPOP_CO")) +
                                    Double.parseDouble((String) row.get("MALE_F50T54_LVPOP_CO")) +
                                    Double.parseDouble((String) row.get("MALE_F55T59_LVPOP_CO"))
                    )
                    .maleTo74(
                            Double.parseDouble((String) row.get("MALE_F60T64_LVPOP_CO")) +
                                    Double.parseDouble((String) row.get("MALE_F65T69_LVPOP_CO")) +
                                    Double.parseDouble((String) row.get("MALE_F70T74_LVPOP_CO"))
                    )
                    .femaleTo19(
                            Double.parseDouble((String) row.get("FEMALE_F0T9_LVPOP_CO")) +
                                    Double.parseDouble((String) row.get("FEMALE_F10T14_LVPOP_CO")) +
                                    Double.parseDouble((String) row.get("FEMALE_F15T19_LVPOP_CO"))
                    )
                    .femaleTo39(
                            Double.parseDouble((String) row.get("FEMALE_F20T24_LVPOP_CO")) +
                                    Double.parseDouble((String) row.get("FEMALE_F25T29_LVPOP_CO")) +
                                    Double.parseDouble((String) row.get("FEMALE_F30T34_LVPOP_CO")) +
                                    Double.parseDouble((String) row.get("FEMALE_F35T39_LVPOP_CO"))
                    )
                    .femaleTo59(
                            Double.parseDouble((String) row.get("FEMALE_F40T44_LVPOP_CO")) +
                                    Double.parseDouble((String) row.get("FEMALE_F45T49_LVPOP_CO")) +
                                    Double.parseDouble((String) row.get("FEMALE_F50T54_LVPOP_CO")) +
                                    Double.parseDouble((String) row.get("FEMALE_F55T59_LVPOP_CO"))
                    )
                    .femaleTo74(
                            Double.parseDouble((String) row.get("FEMALE_F60T64_LVPOP_CO")) +
                                    Double.parseDouble((String) row.get("FEMALE_F65T69_LVPOP_CO")) +
                                    Double.parseDouble((String) row.get("FEMALE_F70T74_LVPOP_CO"))
                    )
                    .build();
            populationDto.setTotal();
            populationRepository.save(populationDto.toEntity());

            return populationDto;
        } catch (Exception exception) {
            log.error(exception.getMessage());
            return null;
        }
    }
}
