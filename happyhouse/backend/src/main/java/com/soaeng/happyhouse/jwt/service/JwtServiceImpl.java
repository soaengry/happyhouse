package com.soaeng.happyhouse.jwt.service;

import com.soaeng.happyhouse.jwt.dto.request.RefreshRequestDto;
import com.soaeng.happyhouse.jwt.dto.response.JwtResponseDto;
import com.soaeng.happyhouse.jwt.entity.RefreshEntity;
import com.soaeng.happyhouse.jwt.repository.RefreshRepository;
import com.soaeng.happyhouse.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private final RefreshRepository refreshRepository;
    private final JwtUtil jwtUtil;
    private static final String REFRESH_ENTITY = "RefreshEntity";
    private static final String REFRESH_TOKEN = "refreshToken";
    private static final String NEW_ACCESS_TOKEN = "newAccessToken";
    private static final String NEW_REFRESH_TOKEN = "newRefreshToken";

    @Transactional
    @Override
    public void addRefresh(String username, String refreshToken) {
        RefreshEntity entity = RefreshEntity.builder()
                .username(username)
                .refresh(refreshToken)
                .build();

        refreshRepository.save(entity);
    }

    @Transactional(readOnly = true)
    @Override
    public Boolean existsRefresh(String refreshToken) {
        return refreshRepository.existsByRefresh(refreshToken);
    }

    @Override
    public void removeRefresh(String refreshToken) {
        refreshRepository.deleteByRefresh(refreshToken);
    }

    @Override
    public void removeRefreshUser(String username) {
        refreshRepository.deleteByUsername(username);
    }

    @Transactional
    @Override
    public JwtResponseDto refreshRotate(RefreshRequestDto dto) {

        String refreshToken = dto.getRefreshToken();

        // Refresh 토큰 검증
        Boolean isValid = jwtUtil.isValid(refreshToken, false);
        if (!isValid) {
            throw new RuntimeException("유효하지 않은 refreshToken입니다.");
        }

        // RefreshEntity 존재 확인 (화이트리스트)
        if (!existsRefresh(refreshToken)) {
            throw new RuntimeException("유효하지 않은 refreshToken입니다.");
        }

        // 정보 추출
        String username = jwtUtil.getUsername(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        // 토큰 생성
        String newAccessToken = jwtUtil.createJWT(username, role, true);
        String newRefreshToken = jwtUtil.createJWT(username, role, false);

        // 기존 Refresh 토큰 DB 삭제 후 신규 추가
        RefreshEntity newRefreshEntity = RefreshEntity.builder()
                .username(username)
                .refresh(newRefreshToken)
                .build();

        removeRefresh(refreshToken);
        refreshRepository.save(newRefreshEntity);

        return new JwtResponseDto(newAccessToken, newRefreshToken);
    }

    @Transactional
    @Override
    public JwtResponseDto cookie2Header(
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        // 쿠키 리스트
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw new RuntimeException("쿠키가 존재하지 않습니다.");
        }

        // Refresh 토큰 획득
        String refreshToken = null;
        for (Cookie cookie : cookies) {
            if (REFRESH_TOKEN.equals(cookie.getName())) {
                refreshToken = cookie.getValue();
                break;
            }
        }

        if (refreshToken == null) {
            throw new RuntimeException("refreshToken 쿠키가 없습니다.");
        }

        // Refresh 토큰 검증
        Boolean isValid = jwtUtil.isValid(refreshToken, false);
        if (!isValid) {
            throw new RuntimeException("유효하지 않은 refreshToken입니다.");
        }

        // 기존 Refresh 토큰 DB 삭제 후 신규 추가
        Map<String, Object> map = reissueJwtToken(refreshToken);
        RefreshEntity newRefreshEntity = (RefreshEntity) map.get(REFRESH_ENTITY);
        removeRefresh(refreshToken);
        refreshRepository.flush(); // 같은 트랜잭션 내부라 : 삭제 -> 생성 문제 해결
        refreshRepository.save(newRefreshEntity);

        // 기존 쿠키 제거
        Cookie refreshCookie = new Cookie(REFRESH_TOKEN, null);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(false);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(10);
        response.addCookie(refreshCookie);

        return new JwtResponseDto((String) map.get(NEW_ACCESS_TOKEN), (String) map.get(NEW_REFRESH_TOKEN));
    }

    private Map<String, Object> reissueJwtToken(String refreshToken) {

        Map<String, Object> map = new HashMap<>();

        // 정보 추출
        String username = jwtUtil.getUsername(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        // 토큰 생성
        String newAccessToken = jwtUtil.createJWT(username, role, true);
        String newRefreshToken = jwtUtil.createJWT(username, role, false);

        map.put(REFRESH_ENTITY, RefreshEntity.builder()
                .username(username)
                .refresh(newRefreshToken)
                .build());
        map.put(NEW_ACCESS_TOKEN, newAccessToken);
        map.put(NEW_REFRESH_TOKEN, newRefreshToken);

        return map;
    }

}
