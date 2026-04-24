package com.soaeng.happyhouse.house.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class BusStopItem {
    @JsonProperty("gpslati")
    private double gpsLati;

    @JsonProperty("gpslong")
    private double gpsLong;

    @JsonProperty("nodeid")
    private String nodeId;

    @JsonProperty("nodenm")
    private String nodeName;

    @JsonProperty("nodeno")
    private int nodeNo;
}
