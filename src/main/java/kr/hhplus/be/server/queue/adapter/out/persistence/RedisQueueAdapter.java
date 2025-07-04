package kr.hhplus.be.server.queue.adapter.out.persistence;

import kr.hhplus.be.server.common.aop.annotation.DistributedLock;
import kr.hhplus.be.server.queue.application.port.out.QueuePort;
import kr.hhplus.be.server.queue.domain.QueueStatus;
import kr.hhplus.be.server.queue.domain.QueueToken;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RedisQueueAdapter implements QueuePort {
    private static final String QUEUE_KEY = "concert:queue";
    private static final String TOKEN_KEY_PREFIX = "concert:token:";
    private static final String USER_TOKEN_KEY = "concert:user:token:"; // 유저별 토큰 매핑
    private static final int TOKEN_EXPIRY_MINUTES = 30;
    private static final int ESTIMATED_PROCESSING_TIME_PER_USER = 5; // 초 단위

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    @DistributedLock(key = "'lock:concert:queue:issueToken-user:' + #userId", waitTime = 5, leaseTime = 3)
    public QueueToken issueToken(Long userId) {
        // 이미 토큰이 발급되었는지 확인
        String existingToken = redisTemplate.opsForValue().get(USER_TOKEN_KEY + userId);
        if (existingToken != null && validateToken(existingToken)) {
            // 기존 토큰이 유효하면 해당 토큰 반환
            Optional<QueueToken> existingQueueToken = getToken(existingToken);
            if (existingQueueToken.isPresent()) {
                QueueToken token = existingQueueToken.get();
                // 현재 대기열 순서 확인
                Long rank = redisTemplate.opsForZSet().rank(QUEUE_KEY, userId.toString());
                int position = (rank != null) ? rank.intValue() + 1 : 1;
                
                return QueueToken.builder()
                        .token(token.getToken())
                        .userId(userId)
                        .position(position)
                        .issuedAt(token.getIssuedAt())
                        .expiresAt(token.getExpiresAt())
                        .active(true)
                        .build();
            }
        }

        String token = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusMinutes(TOKEN_EXPIRY_MINUTES);

        int position = 0; // 대기열 순서 초기화

        // Redis 의 Sorted Set(정렬된 집합) 자료구조에 지금까지 들어온 사용자를 대기열에 추가 (score는 현재 시간의 타임스탬프)
        // ZSET은 자동으로 정렬되므로, 시간 순서대로 대기열이 유지됨
        redisTemplate.opsForZSet().add(
                QUEUE_KEY,                          // 1. 대기열 키: "concert:queue"
                userId.toString(),                  // 2. 값: 유저 ID (String 형태로 저장)
                now.toEpochSecond(ZoneOffset.UTC) * 1_000_000_000L
                        + now.getNano()                   // 3. 정렬 기준(score): 현재 시간 (초 단위)
        );

        // Redis의 Hash 자료구조를 사용하여 토큰 정보를 저장
        String tokenKey = TOKEN_KEY_PREFIX + token;
        redisTemplate.opsForHash().put(tokenKey, "userId", userId.toString());
        redisTemplate.opsForHash().put(tokenKey, "issuedAt", now.toString());
        redisTemplate.opsForHash().put(tokenKey, "expiresAt", expiresAt.toString());
        redisTemplate.opsForHash().put(tokenKey, "active", "true"); // 활성 상태 표시 (토큰이 유효함을 나타냄)
        redisTemplate.expire(tokenKey, TOKEN_EXPIRY_MINUTES, TimeUnit.MINUTES);

        // 유저별 토큰 매핑 저장
        redisTemplate.opsForValue().set(USER_TOKEN_KEY + userId, token, TOKEN_EXPIRY_MINUTES, TimeUnit.MINUTES);

        Long rank = redisTemplate.opsForZSet()  // Redis의 Sorted Set(ZSET) 기능에 접근
                .rank(QUEUE_KEY, userId.toString());    // 해당 유저가 몇 번째인지 0부터 시작하는 순위를 가져옴
        if (rank == null) { // 유저가 대기열에 추가되지 않은 경우 (방어 코드)
            throw new IllegalStateException("User not found in queue after adding");
        }
        // 현재 대기 순서 계산
        position = rank.intValue() + 1; // 순위는 0부터 시작하므로 +1을 해서 1부터 시작하는 Long 타입 순위로 변환

        return QueueToken.builder()
                .token(token)
                .userId(userId)
                .position(position)
                .issuedAt(now)
                .expiresAt(expiresAt)
                .active(true)
                .build();
    }

    @Override
    public Optional<QueueStatus> getStatus(String token) {
        String tokenKey = TOKEN_KEY_PREFIX + token;
        if (!redisTemplate.hasKey(tokenKey)) {  // 전체 Redis 키 중에서 해당 토큰 키가 존재하는지 확인 (모든 Redis 자료구조 통합 검색)
            return Optional.empty();
        }

        String userId = (String) redisTemplate.opsForHash().get(tokenKey, "userId");
        if (userId == null) {
            return Optional.empty();
        }

        Long position = redisTemplate.opsForZSet().rank(QUEUE_KEY, userId);
        if (position == null) {
            return Optional.empty();
        }

        Long totalUsers = redisTemplate.opsForZSet().size(QUEUE_KEY);
        if (totalUsers == null) {
            totalUsers = 0L; // 대기열에 사용자가 없을 경우
        }

        long estimatedWaitTime = (position + 1) * ESTIMATED_PROCESSING_TIME_PER_USER;
        // 예상 대기 시간은 현재 사용자의 순서(position)와 각 사용자 처리 시간(ESTIMATED_PROCESSING_TIME_PER_USER)을 곱하여 계산
        if (estimatedWaitTime < 0) {
            estimatedWaitTime = 0; // 예상 대기 시간이 음수가 되는 경우 방지
        }

        return Optional.of(QueueStatus.builder()
                .position(position.intValue() + 1)
                .totalUsers(totalUsers.intValue())
                .estimatedWaitTime(estimatedWaitTime)
                .isActive(true) // 토큰이 유효한 경우 항상 활성 상태로 간주
                .build());
    }

    @Override
    public void removeToken(String token) {
        String tokenKey = TOKEN_KEY_PREFIX + token;
        String userId = (String) redisTemplate.opsForHash().get(tokenKey, "userId");
        if (userId != null) {
            redisTemplate.opsForZSet().remove(QUEUE_KEY, userId);
            // 유저별 토큰 매핑도 삭제
            redisTemplate.delete(USER_TOKEN_KEY + userId);
        }
        redisTemplate.delete(tokenKey);
    }

    @Override
    public boolean validateToken(String token) {
        String tokenKey = TOKEN_KEY_PREFIX + token;
        if (!redisTemplate.hasKey(tokenKey)) {
            return false;
        }

        String active = (String) redisTemplate.opsForHash().get(tokenKey, "active");
        String expiresAt = (String) redisTemplate.opsForHash().get(tokenKey, "expiresAt");
        
        return "true".equals(active) && 
               LocalDateTime.parse(expiresAt).isAfter(LocalDateTime.now());
    }

    @Override
    public Optional<QueueToken> getToken(String token) {
        String tokenKey = TOKEN_KEY_PREFIX + token;
        if (!redisTemplate.hasKey(tokenKey)) {
            return Optional.empty();
        }

        String userId = (String) redisTemplate.opsForHash().get(tokenKey, "userId");
        String issuedAt = (String) redisTemplate.opsForHash().get(tokenKey, "issuedAt");
        String expiresAt = (String) redisTemplate.opsForHash().get(tokenKey, "expiresAt");
        String active = (String) redisTemplate.opsForHash().get(tokenKey, "active");

        if (userId == null || issuedAt == null || expiresAt == null || active == null) {
            return Optional.empty();
        }

        return Optional.of(QueueToken.builder()
                .token(token)
                .userId(Long.parseLong(userId))
                .issuedAt(LocalDateTime.parse(issuedAt))
                .expiresAt(LocalDateTime.parse(expiresAt))
                .active(Boolean.parseBoolean(active))
                .build());
    }

    @Override
    @DistributedLock(key = "'queue:lock'")
    public boolean isFirstInQueue(String token) {
        String tokenKey = TOKEN_KEY_PREFIX + token;
        if (!redisTemplate.hasKey(tokenKey)) return false;

        String userId = (String) redisTemplate.opsForHash().get(tokenKey, "userId");
        if (userId == null) return false;

        Long position = redisTemplate.opsForZSet().rank(QUEUE_KEY, userId);
        return position != null && position == 0; // 0이면 첫 번째
    }

    @Override
    @DistributedLock(key = "'queue:lock:removeExpiredTokens'")
    @Scheduled(fixedRate = 60000) // 1분마다 실행
    public void removeExpiredTokens() {
        Set<String> tokens = redisTemplate.keys(TOKEN_KEY_PREFIX + "*");
        if (tokens == null) return;

        LocalDateTime now = LocalDateTime.now();
        for (String tokenKey : tokens) {
            String expiresAt = (String) redisTemplate.opsForHash().get(tokenKey, "expiresAt");
            if (expiresAt != null && LocalDateTime.parse(expiresAt).isBefore(now)) {
                String userId = (String) redisTemplate.opsForHash().get(tokenKey, "userId");
                if (userId != null) {
                    redisTemplate.opsForZSet().remove(QUEUE_KEY, userId);
                    // 유저별 토큰 매핑도 삭제
                    redisTemplate.delete(USER_TOKEN_KEY + userId);
                }
                redisTemplate.delete(tokenKey);
            }
        }
    }
} 