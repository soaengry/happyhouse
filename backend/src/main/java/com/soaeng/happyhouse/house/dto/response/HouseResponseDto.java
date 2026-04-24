package com.soaeng.happyhouse.house.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class HouseResponseDto {

    private int result;

    private List<HouseDto> houseList;

    private int count;

}
