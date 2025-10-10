package com.soaeng.happyhouse.house.controller;

import com.soaeng.happyhouse.house.dto.DongDto;
import com.soaeng.happyhouse.house.dto.GugunDto;
import com.soaeng.happyhouse.house.dto.SidoDto;
import com.soaeng.happyhouse.house.service.HouseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AddressController {

    private final HouseService service;

    @GetMapping("/sido")
    public ResponseEntity<List<SidoDto>> getSidoList() {
        List<SidoDto> sidoList = service.getSidoList();

        return sidoList != null ?
                new ResponseEntity<>(sidoList, HttpStatus.OK) :
                new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/gugun/{sidoCode}")
    public ResponseEntity<List<GugunDto>> getGugunList(@PathVariable Long sidoCode) {
        List<GugunDto> gugunList = service.getGugunList(sidoCode);

        return gugunList != null ?
                new ResponseEntity<>(gugunList, HttpStatus.OK) :
                new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/dong/{gugunCode}")
    public ResponseEntity<List<DongDto>> getDongList(@PathVariable Long gugunCode) {
        List<DongDto> dongList = service.getDongList(gugunCode);

        return dongList != null ?
                new ResponseEntity<>(dongList, HttpStatus.OK) :
                new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
