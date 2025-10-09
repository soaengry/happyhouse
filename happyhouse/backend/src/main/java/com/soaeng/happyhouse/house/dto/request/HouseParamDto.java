package com.soaeng.happyhouse.house.dto.request;

import lombok.*;

@Data
public class HouseParamDto {

    // 데이터 양
    private int limit;

    // 생략할 데이터
    private int offset;

    // 검색 종류
    private String type;

    // 시도 코드
    private long sidoCode;

    // 구군 코드
    private long gugunCode;

    // 동 코드
    private long dongCode;

    // 검색어
    private String keyword;

    // 건물 번호
    private long aptNo;

}
