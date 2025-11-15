package com.soaeng.happyhouse.house.controller;

import com.soaeng.happyhouse.house.dto.response.BaseAddressDto;
import com.soaeng.happyhouse.house.dto.response.HouseResponseDto;
import com.soaeng.happyhouse.house.service.BookmarkServiceImpl;
import com.soaeng.happyhouse.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/bookmark")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkServiceImpl bookmarkService;

    @PostMapping("/house/{aptCode}")
    public ResponseEntity<?> addBookmarkHouse(@AuthenticationPrincipal UserEntity user, @PathVariable Long aptCode) {
        return bookmarkService.addBookmarkHouse(user, aptCode) ?
                new ResponseEntity<>(HttpStatus.CREATED) :
                new ResponseEntity<>(HttpStatus.CONFLICT);
    }

    @DeleteMapping("/house/{aptCode}")
    public ResponseEntity<?> removeBookmarkHouse(@AuthenticationPrincipal UserEntity user,
                                                 @PathVariable Long aptCode) {
        bookmarkService.removeBookmarkHouse(user, aptCode);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/house")
    public ResponseEntity<HouseResponseDto> getBookmarkHouses(@AuthenticationPrincipal UserEntity user) {
        HouseResponseDto response = bookmarkService.getBookmarkHouseResponse(user);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PostMapping("/region/{dongCode}")
    public ResponseEntity<?> addBookmarkRegion(@AuthenticationPrincipal UserEntity user, @PathVariable Long dongCode) {
        return bookmarkService.addBookmarkRegion(user, dongCode) ?
                new ResponseEntity<>(HttpStatus.CREATED) :
                new ResponseEntity<>(HttpStatus.CONFLICT);
    }

    @DeleteMapping("/region/{dongCode}")
    public ResponseEntity<?> removeBookmarkRegion(@AuthenticationPrincipal UserEntity user,
                                                  @PathVariable Long dongCode) {
        bookmarkService.removeBookmarkRegion(user, dongCode);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/region")
    public ResponseEntity<List<BaseAddressDto>> getBookmarkRegions(@AuthenticationPrincipal UserEntity user) {
        List<BaseAddressDto> response = bookmarkService.getBookmarkRegionResponse(user);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
