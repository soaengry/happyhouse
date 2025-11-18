package com.soaeng.happyhouse.house.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@ToString
@Table(name = "population")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Population {
    @Id
    @Column(length = 8)
    private String adstrdCode;

    @Column(name = "total_local")
    private Double totalLocal;

    @Column(name = "total_male")
    private Double totalMale;
    @Column(name = "male_to_19")
    private Double maleTo19;
    @Column(name = "male_to_39")
    private Double maleTo39;
    @Column(name = "male_to_59")
    private Double maleTo59;
    @Column(name = "male_to_74")
    private Double maleTo74;

    @Column(name = "total_female")
    private Double totalFemale;
    @Column(name = "female_to_19")
    private Double femaleTo19;
    @Column(name = "female_to_39")
    private Double femaleTo39;
    @Column(name = "female_to_59")
    private Double femaleTo59;
    @Column(name = "female_to_74")
    private Double femaleTo74;
}
