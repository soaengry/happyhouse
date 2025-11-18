package com.soaeng.happyhouse.house.dto.response;

import com.soaeng.happyhouse.house.entity.Population;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PopulationDto {

    private String adstrdCode;
    private double totalLocal;

    private double totalMale;
    private double maleTo19;
    private double maleTo39;
    private double maleTo59;
    private double maleTo74;

    private double totalFemale;
    private double femaleTo19;
    private double femaleTo39;
    private double femaleTo59;
    private double femaleTo74;

    public void setTotal() {
        this.totalMale = this.maleTo19 + this.maleTo39 + this.maleTo59 + this.maleTo74;
        this.totalFemale = this.femaleTo19 + this.femaleTo39 + this.femaleTo59 + this.femaleTo74;
    }

    public Population toEntity() {
        return Population.builder()
                .adstrdCode(this.adstrdCode)
                .totalLocal(this.totalLocal)
                .totalMale(this.totalMale)
                .maleTo19(this.maleTo19)
                .maleTo39(this.maleTo39)
                .maleTo59(this.maleTo59)
                .maleTo74(this.maleTo74)
                .totalFemale(this.totalFemale)
                .femaleTo19(this.femaleTo19)
                .femaleTo39(this.femaleTo39)
                .femaleTo59(this.femaleTo59)
                .femaleTo74(this.femaleTo74)
                .build();
    }
}
