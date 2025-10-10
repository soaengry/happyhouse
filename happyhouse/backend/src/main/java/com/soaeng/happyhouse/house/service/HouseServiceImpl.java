package com.soaeng.happyhouse.house.service;

import com.soaeng.happyhouse.house.dao.HouseDao;
import com.soaeng.happyhouse.house.dto.BaseAddressDto;
import com.soaeng.happyhouse.house.dto.DongDto;
import com.soaeng.happyhouse.house.dto.GugunDto;
import com.soaeng.happyhouse.house.dto.SidoDto;
import com.soaeng.happyhouse.house.dto.request.HouseParamDto;
import com.soaeng.happyhouse.house.dto.response.HouseDto;
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
    public List<HouseDto> getAllDealList(HouseParamDto param) {
        List<HouseDto> houseDtoList = houseDao.getAllDealList(param);
        setBaseAddressList(houseDtoList);

        return houseDtoList;
    }

    @Override
    public int getAllDealCount() {
        return houseDao.getAllDealCount();
    }

    @Override
    public List<HouseDto> getGugunDealList(HouseParamDto param) {
        List<HouseDto> houseDtoList = houseDao.getGugunDealList(param);
        setBaseAddressList(houseDtoList);

        return houseDtoList;
    }

    @Override
    public int getGugunDealCount(Long gugunCode) {
        return houseDao.getGugunDealCount(gugunCode);
    }

    @Override
    public List<HouseDto> getDongDealList(HouseParamDto param) {
        List<HouseDto> houseDtoList = houseDao.getDongDealList(param);
        setBaseAddressList(houseDtoList);

        return houseDtoList;
    }

    @Override
    public int getDongDealCount(Long dongCode) {
        return houseDao.getDongDealCount(dongCode);
    }

    @Override
    public List<HouseDto> getKeywordDealList(HouseParamDto param) {
        List<HouseDto> houseDtoList = houseDao.getKeywordDealList(param);
        setBaseAddressList(houseDtoList);

        return houseDtoList;
    }

    @Override
    public int getKeywordDealCount(String keyword) {
        return houseDao.getKeywordDealCount(keyword);
    }

    @Override
    public List<HouseDto> getSidoKeywordDealList(HouseParamDto param) {
        List<HouseDto> houseDtoList = houseDao.getSidoKeywordDealList(param);
        setBaseAddressList(houseDtoList);

        return houseDtoList;
    }

    @Override
    public int getSidoKeywordDealCount(Map<String, Object> param) {
        return houseDao.getSidoKeywordDealCount(param);
    }

    @Override
    public List<HouseDto> getGugunKeywordDealList(HouseParamDto param) {
        List<HouseDto> houseDtoList = houseDao.getGugunKeywordDealList(param);
        setBaseAddressList(houseDtoList);

        return houseDtoList;
    }

    @Override
    public int getGugunKeywordDealCount(Map<String, Object> param) {
        return houseDao.getGugunKeywordDealCount(param);
    }

    @Override
    public List<HouseDto> getDongKeywordDealList(HouseParamDto param) {
        List<HouseDto> houseDtoList = houseDao.getDongKeywordDealList(param);
        setBaseAddressList(houseDtoList);

        return houseDtoList;
    }

    @Override
    public int getDongKeywordDealCount(Map<String, Object> param) {
        return houseDao.getDongKeywordDealCount(param);
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
}
