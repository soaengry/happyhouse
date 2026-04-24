package com.soaeng.happyhouse.house.service;

import com.soaeng.happyhouse.external.ApiExplorer;
import com.soaeng.happyhouse.house.dao.HouseDao;
import com.soaeng.happyhouse.house.dto.request.HouseParamDto;
import com.soaeng.happyhouse.house.dto.response.*;
import com.soaeng.happyhouse.house.entity.BookmarkHouse;
import com.soaeng.happyhouse.house.entity.SubwayStation;
import com.soaeng.happyhouse.house.repository.BookmarkHouseRepository;
import com.soaeng.happyhouse.house.repository.SubwayStationRepository;
import com.soaeng.happyhouse.user.entity.UserEntity;
import com.soaeng.happyhouse.util.GeoUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HouseServiceImpl implements HouseService {

    private static final double SUBWAY_RADIUS_METERS = 1000.0;
    private static final double LAT_DELTA = 0.009;
    private static final double LNG_DELTA = 0.011;

    private final HouseDao houseDao;
    private final BookmarkHouseRepository bookmarkHouseRepository;
    private final SubwayStationRepository subwayStationRepository;
    private final ApiExplorer apiExplorer;

    @Override
    public BaseAddressDto getBaseAddress(Long dongCode) {
        return houseDao.getBaseAddress(dongCode);
    }

    @Override
    public List<SidoDto> getSidoList() {
        return houseDao.getSidoList();
    }

    @Override
    public List<GugunDto> getGugunList(Long sidoCode) {
        return houseDao.getGugunList(sidoCode);
    }

    @Override
    public List<DongDto> getDongList(Long gugunCode) {
        return houseDao.getDongList(gugunCode);
    }

    @Override
    public List<HouseDto> getHouseList(UserEntity user, HouseParamDto param) {
        List<HouseDto> list = houseDao.getHouseList(param);
        if (!list.isEmpty()) {
            setBaseAddressList(list);
            setBookmarkList(user, list);
        }
        return list;
    }

    @Override
    public int getHouseCount(HouseParamDto param) {
        return houseDao.getHouseCount(param);
    }

    @Override
    public List<HouseDto> getHouseDealList(Long aptCode) {
        return houseDao.getHouseDealList(aptCode);
    }

    @Override
    public int getHouseDealCount(Long aptCode) {
        return houseDao.getHouseDealCount(aptCode);
    }

    @Override
    public PopulationDto getPopulation(Long dongCode) {
        String adstrdCode = houseDao.getAdstrdCode(dongCode);
        if (adstrdCode == null) return null;

        PopulationDto cached = houseDao.getPopulation(adstrdCode);
        if (cached != null) return cached;

        return apiExplorer.getPopulation(adstrdCode);
    }

    @Override
    public List<SubwayStationDto> getNearbySubwayStations(double lat, double lng) {
        List<SubwayStation> candidates = subwayStationRepository.findNearbyStations(
                lat - LAT_DELTA, lat + LAT_DELTA,
                lng - LNG_DELTA, lng + LNG_DELTA
        );

        return candidates.stream()
                .map(st -> new SubwayStationDto(
                        st.getBldnNm(),
                        st.getRoute(),
                        GeoUtil.distance(lat, lng, Double.parseDouble(st.getLat()), Double.parseDouble(st.getLot()))
                ))
                .filter(dto -> dto.getDistance() <= SUBWAY_RADIUS_METERS)
                .sorted(Comparator.comparingDouble(SubwayStationDto::getDistance))
                .toList();
    }

    // IN 쿼리로 N+1 방지
    private void setBaseAddressList(List<HouseDto> list) {
        List<Long> dongCodes = list.stream().map(HouseDto::getDongCode).distinct().toList();
        Map<Long, BaseAddressDto> addressMap = houseDao.getBaseAddressByDongCodes(dongCodes)
                .stream()
                .collect(Collectors.toMap(BaseAddressDto::getDongCode, Function.identity()));
        list.forEach(dto -> dto.setAddress(addressMap.get(dto.getDongCode())));
    }

    private void setBookmarkList(UserEntity user, List<HouseDto> list) {
        Set<Long> bookmarkedAptCodes = bookmarkHouseRepository.findByUser(user)
                .stream()
                .map(BookmarkHouse::getAptCode)
                .collect(Collectors.toSet());
        list.forEach(dto -> dto.setBookmark(bookmarkedAptCodes.contains((long) dto.getAptCode())));
    }
}
