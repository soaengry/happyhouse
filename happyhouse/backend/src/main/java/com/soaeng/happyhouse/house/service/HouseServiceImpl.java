package com.soaeng.happyhouse.house.service;

import com.soaeng.happyhouse.house.dao.HouseDao;
import com.soaeng.happyhouse.house.dto.BaseAddressDto;
import com.soaeng.happyhouse.house.dto.DongDto;
import com.soaeng.happyhouse.house.dto.GugunDto;
import com.soaeng.happyhouse.house.dto.SidoDto;
import com.soaeng.happyhouse.house.dto.request.HouseParamDto;
import com.soaeng.happyhouse.house.dto.response.HouseDto;
import com.soaeng.happyhouse.house.entity.BookmarkHouse;
import com.soaeng.happyhouse.house.repository.BookmarkHouseRepository;
import com.soaeng.happyhouse.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class HouseServiceImpl implements HouseService {

    private final HouseDao houseDao;
    private final BookmarkHouseRepository bookmarkHouseRepository;

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
    public List<HouseDto> getAllHouseList(UserEntity user, HouseParamDto param) {
        List<HouseDto> houseDtoList = houseDao.getAllHouseList(param);
        setBaseAddressList(houseDtoList);
        setBookmarkList(user, houseDtoList);

        return houseDtoList;
    }

    @Override
    public int getAllHouseCount() {
        return houseDao.getAllHouseCount();
    }

    @Override
    public List<HouseDto> getSidoHouseList(UserEntity user, HouseParamDto param) {
        List<HouseDto> houseDtoList = houseDao.getAllHouseList(param);
        setBaseAddressList(houseDtoList);
        setBookmarkList(user, houseDtoList);

        return houseDtoList;
    }

    @Override
    public int getSidoHouseCount(Long sidoCode) {
        return houseDao.getSidoHouseCount(sidoCode);
    }

    @Override
    public List<HouseDto> getGugunHouseList(UserEntity user, HouseParamDto param) {
        List<HouseDto> houseDtoList = houseDao.getGugunHouseList(param);
        setBaseAddressList(houseDtoList);
        setBookmarkList(user, houseDtoList);

        return houseDtoList;
    }

    @Override
    public int getGugunHouseCount(Long gugunCode) {
        return houseDao.getGugunHouseCount(gugunCode);
    }

    @Override
    public List<HouseDto> getDongHouseList(UserEntity user, HouseParamDto param) {
        List<HouseDto> houseDtoList = houseDao.getDongHouseList(param);
        setBaseAddressList(houseDtoList);
        setBookmarkList(user, houseDtoList);

        return houseDtoList;
    }

    @Override
    public int getDongHouseCount(Long dongCode) {
        return houseDao.getDongHouseCount(dongCode);
    }

    @Override
    public List<HouseDto> getKeywordHouseList(UserEntity user, HouseParamDto param) {
        List<HouseDto> houseDtoList = houseDao.getKeywordHouseList(param);
        setBaseAddressList(houseDtoList);
        setBookmarkList(user, houseDtoList);

        return houseDtoList;
    }

    @Override
    public int getKeywordHouseCount(String keyword) {
        return houseDao.getKeywordHouseCount(keyword);
    }

    @Override
    public List<HouseDto> getSidoKeywordHouseList(UserEntity user, HouseParamDto param) {
        List<HouseDto> houseDtoList = houseDao.getSidoKeywordHouseList(param);
        setBaseAddressList(houseDtoList);
        setBookmarkList(user, houseDtoList);

        return houseDtoList;
    }

    @Override
    public int getSidoKeywordHouseCount(Map<String, Object> param) {
        return houseDao.getSidoKeywordHouseCount(param);
    }

    @Override
    public List<HouseDto> getGugunKeywordHouseList(UserEntity user, HouseParamDto param) {
        List<HouseDto> houseDtoList = houseDao.getGugunKeywordHouseList(param);
        setBaseAddressList(houseDtoList);
        setBookmarkList(user, houseDtoList);

        return houseDtoList;
    }

    @Override
    public int getGugunKeywordHouseCount(Map<String, Object> param) {
        return houseDao.getGugunKeywordHouseCount(param);
    }

    @Override
    public List<HouseDto> getDongKeywordHouseList(UserEntity user, HouseParamDto param) {
        List<HouseDto> houseDtoList = houseDao.getDongKeywordHouseList(param);
        setBaseAddressList(houseDtoList);
        setBookmarkList(user, houseDtoList);

        return houseDtoList;
    }

    @Override
    public int getDongKeywordHouseCount(Map<String, Object> param) {
        return houseDao.getDongKeywordHouseCount(param);
    }

    @Override
    public List<HouseDto> getHouseDealList(Integer aptCode) {
        return houseDao.getHouseDealList(aptCode);
    }

    @Override
    public int getHouseDealCount(Integer aptCode) {
        return houseDao.getHouseDealCount(aptCode);
    }

    private void setBaseAddressList(List<HouseDto> houseDtoList) {
        houseDtoList.forEach(houseDto -> houseDto.setAddress(houseDao.getBaseAddress(houseDto.getDongCode())));
    }

    private void setBookmarkList(UserEntity user, List<HouseDto> houseDtoList) {
        List<Long> bookmarkHouseList = bookmarkHouseRepository.findByUser(user)
                .stream()
                .map(BookmarkHouse::getAptCode)
                .toList();

        houseDtoList.forEach(houseDto -> houseDto.setBookmark(bookmarkHouseList.contains((long) houseDto.getAptCode())));
    }
}
