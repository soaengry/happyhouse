package com.soaeng.happyhouse.external;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soaeng.happyhouse.house.dto.response.BusStopItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class ApiExplorer {

    @Value("${key.data-go-kr}")
    private String SERVICE_KEY;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<BusStopItem> getNearbyBusStops(String gpsLati, String gpsLong) {
        try {
            StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1613000/BusSttnInfoInqireService/getCrdntPrxmtSttnList"); /*URL*/
            urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + SERVICE_KEY); /*Service Key*/
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
}
