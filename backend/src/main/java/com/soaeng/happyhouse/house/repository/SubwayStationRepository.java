package com.soaeng.happyhouse.house.repository;

import com.soaeng.happyhouse.house.entity.SubwayStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SubwayStationRepository extends JpaRepository<SubwayStation, String> {

    // lat/lot이 VARCHAR 컬럼이므로 CAST를 사용한 bounding box 필터
    @Query(value = """
            SELECT * FROM subway_stations
            WHERE CAST(lat AS DECIMAL(10,7)) BETWEEN :minLat AND :maxLat
              AND CAST(lot AS DECIMAL(10,7)) BETWEEN :minLng AND :maxLng
            """, nativeQuery = true)
    List<SubwayStation> findNearbyStations(
            @Param("minLat") double minLat, @Param("maxLat") double maxLat,
            @Param("minLng") double minLng, @Param("maxLng") double maxLng
    );
}
