package kr.hhplus.be.server.concert.application.port.out;

import java.time.Duration;

public interface ReservationLockPort {
    void lockSeat(Long seatId, Long userId, Duration ttl);
    boolean isSeatLocked(Long seatId);
    void releaseSeat(Long seatId);
}

