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

    @Override
    public void lockSeat(Long seatId, Long userId, Duration ttl) {
        String key = "seat:" + seatId;
        redisTemplate.opsForValue().setIfAbsent(key, userId.toString(), ttl);
    }

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

    @Override
    public void releaseSeat(Long seatId) {
        String key = "seat:" + seatId;
        redisTemplate.delete(key);
    }
}

