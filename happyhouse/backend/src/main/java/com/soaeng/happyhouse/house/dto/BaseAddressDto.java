package com.soaeng.happyhouse.house.dto;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class BaseAddressDto {

    private long no;

    private String sidoName;

    private String gugunName;

    private String dongName;

    private long dongCode;

    private String lat;

    private String lng;

}
