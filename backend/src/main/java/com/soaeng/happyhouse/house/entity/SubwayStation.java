package com.soaeng.happyhouse.house.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "subway_stations")
@Getter
public class SubwayStation {
    @Id
    private String bldnId;   // 역사_ID
    private String bldnNm;   // 역사명
    private String route;    // 호선
    private String lat;      // 위도
    private String lot;      // 경도
}
