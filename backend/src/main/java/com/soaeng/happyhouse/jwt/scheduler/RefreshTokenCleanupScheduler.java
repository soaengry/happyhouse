package com.soaeng.happyhouse.jwt.scheduler;

import com.soaeng.happyhouse.jwt.repository.RefreshRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class RefreshTokenCleanupScheduler {

    private final RefreshRepository refreshRepository;

    @Value("${jwt.refresh-ttl:604800}")
    private long refreshTtlSeconds;

    // 만료된 Refresh 토큰 주기적 삭제 (매일 새벽 3시)
    @Scheduled(cron = "0 0 3 * * *")
    public void deleteExpiredRefreshTokens() {
        LocalDateTime cutoff = LocalDateTime.now().minusSeconds(refreshTtlSeconds);
        refreshRepository.deleteByCreatedDateBefore(cutoff);
    }
}
