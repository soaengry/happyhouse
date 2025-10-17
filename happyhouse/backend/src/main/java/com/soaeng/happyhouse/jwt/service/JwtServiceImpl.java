package com.soaeng.happyhouse.jwt.service;

import com.soaeng.happyhouse.jwt.entity.RefreshEntity;
import com.soaeng.happyhouse.jwt.repository.RefreshRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private final RefreshRepository refreshRepository;

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
}
