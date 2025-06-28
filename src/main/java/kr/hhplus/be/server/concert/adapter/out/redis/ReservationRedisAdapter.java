package kr.hhplus.be.server.concert.adapter.out.redis;

import kr.hhplus.be.server.concert.application.port.out.ReservationLockPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class ReservationRedisAdapter implements ReservationLockPort {

    private final StringRedisTemplate redisTemplate;

    /*
     * 락 획득: setIfAbsent() (SETNX + TTL)
     */
    @Override
    public void lockSeat(Long seatId, Long userId, Duration ttl) {
        String key = "seat:" + seatId;
        // Redis의 SET key value NX EX seconds 명령어 사용
        // NX: 키가 없을 때만 설정 (SETNX)
        // EX: 만료 시간 설정 (TTL)
        // # Redis CLI에서 실행되는 명령어
        // SET seat:123 "456" NX EX 300
        redisTemplate.opsForValue().setIfAbsent(key, userId.toString(), ttl);
    }

    /*
     * 락 확인: hasKey()
     */
    @Override
    public Long getReservedUserId(Long seatId) {
        String key = "seat:" + seatId;
        return Long.parseLong(redisTemplate.opsForValue().get(key));
    }

    @Override
    public boolean isSeatLocked(Long seatId) {
        String key = "seat:" + seatId;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /*
     * 락 해제: delete()
     */
    @Override
    public void releaseSeat(Long seatId) {
        String key = "seat:" + seatId;
        // DEL key 명령어로 락 해제
        redisTemplate.delete(key);
    }
}

