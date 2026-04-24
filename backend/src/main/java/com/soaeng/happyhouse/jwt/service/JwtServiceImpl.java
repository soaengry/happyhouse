package com.soaeng.happyhouse.jwt.service;

import com.soaeng.happyhouse.exception.BusinessException;
import com.soaeng.happyhouse.exception.ErrorCode;
import com.soaeng.happyhouse.jwt.dto.request.RefreshRequestDto;
import com.soaeng.happyhouse.jwt.dto.response.JwtResponseDto;
import com.soaeng.happyhouse.jwt.entity.RefreshEntity;
import com.soaeng.happyhouse.jwt.repository.RefreshRepository;
import com.soaeng.happyhouse.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private final RefreshRepository refreshRepository;
    private final JwtUtil jwtUtil;

    private static final String REFRESH_COOKIE_NAME = "refreshToken";

    @Value("${app.cookie.secure:false}")
    private boolean cookieSecure;

    @Transactional
    @Override
    public void addRefresh(String username, String refreshToken) {
        refreshRepository.save(RefreshEntity.builder()
                .username(username)
                .refresh(refreshToken)
                .build());
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
        return reissueToken(dto.getRefreshToken());
    }

    @Transactional
    @Override
    public JwtResponseDto cookie2Header(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractRefreshCookie(request);
        JwtResponseDto result = reissueToken(refreshToken);

        // 단명 쿠키 무효화 (교환 완료)
        Cookie expiredCookie = new Cookie(REFRESH_COOKIE_NAME, null);
        expiredCookie.setHttpOnly(true);
        expiredCookie.setSecure(cookieSecure);
        expiredCookie.setPath("/");
        expiredCookie.setMaxAge(0);
        response.addCookie(expiredCookie);

        return result;
    }

    // Refresh 토큰 검증 → Rotation → 새 토큰 쌍 반환
    private JwtResponseDto reissueToken(String refreshToken) {
        if (!jwtUtil.isValid(refreshToken, false)) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }
        if (!existsRefresh(refreshToken)) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }

        String username = jwtUtil.getUsername(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        String newAccessToken = jwtUtil.createJWT(username, role, true);
        String newRefreshToken = jwtUtil.createJWT(username, role, false);

        removeRefresh(refreshToken);
        refreshRepository.flush(); // 같은 트랜잭션: 삭제 후 즉시 저장
        addRefresh(username, newRefreshToken);

        return new JwtResponseDto(newAccessToken, newRefreshToken);
    }

    private String extractRefreshCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw new BusinessException(ErrorCode.TOKEN_NOT_FOUND);
        }
        for (Cookie cookie : cookies) {
            if (REFRESH_COOKIE_NAME.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        throw new BusinessException(ErrorCode.TOKEN_NOT_FOUND);
    }
}
