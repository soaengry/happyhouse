package com.soaeng.happyhouse.house.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsDto {
    private String url;
    private String title;
    private String content;
    private String publish;
    private String date;
    private String img;
}
