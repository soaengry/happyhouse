package com.soaeng.happyhouse.board.dto.request;

import lombok.Data;

@Data
public class BoardParamDto {

    private int limit = 10;
    private int offset = 0;
    private String searchType; // title | content | all
    private String keyword;
}
