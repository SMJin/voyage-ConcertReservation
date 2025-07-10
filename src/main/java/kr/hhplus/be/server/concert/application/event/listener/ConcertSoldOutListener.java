package kr.hhplus.be.server.concert.application.event.listener;

import kr.hhplus.be.server.concert.application.event.ConcertSoldOutEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConcertSoldOutListener {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${app.redis.key.soldout-ranking}")
    private String soldoutRankingKey;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(ConcertSoldOutEvent event) {
        try {
            redisTemplate.opsForZSet().add(soldoutRankingKey, event.getConcertId(), event.getSoldoutTime());
        } catch (Exception e) {
            log.warn("[매진 Redis 기록 실패] concertId: {}, error: {}", event.getConcertId(), e.getMessage(), e);
        }
    }
}
