package com.soaeng.happyhouse.house.repository;

import com.soaeng.happyhouse.house.entity.Population;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PopulationRepository extends JpaRepository<Population, String> {
}
