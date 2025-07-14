package kr.hhplus.be.server.concert.application.event.listener;

import kr.hhplus.be.server.concert.application.event.ReservationCreatedEvent;
import kr.hhplus.be.server.concert.application.port.out.ReservationLockPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationCreatedListener {

    private final ReservationLockPort reservationLockPort;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(ReservationCreatedEvent event) {
        try {
            reservationLockPort.lockSeat(
                    event.getSeatId(),
                    event.getUserId(),
                    Duration.ofMinutes(5)   // 5분 고정
            );
        } catch (Exception e) {
            log.warn("[Redis 좌석 락 실패] seatId: {}, userId: {}, reason: {}",
                    event.getSeatId(), event.getUserId(), e.getMessage(), e);
        }
    }
}
