package com.soaeng.happyhouse.house.dto.response;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class HouseDto {

    // 거래 번호
    private long no;

    // 건물 번호
    private int aptCode;

    // 건물명
    private String aptName;

    // 동 코드
    private long dongCode;

    // 면적
    private String area;

    // 층
    private String floor;

    // 위도
    private String lat;

    // 경도
    private String lng;

    // 거래 금액
    private String dealAmount;

    // 거래 년도
    private String dealYear;

    // 거래 월
    private String dealMonth;

    // 거래 일
    private String dealDay;

    // 법정동
    private String address;

}
