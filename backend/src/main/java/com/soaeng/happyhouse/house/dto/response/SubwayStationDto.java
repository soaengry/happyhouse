package com.soaeng.happyhouse.house.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SubwayStationDto {
    private String bldnNm;  // 역사명
    private String route;   // 호선
    private double distance; // 입력 좌표와의 거리 (m)
}
