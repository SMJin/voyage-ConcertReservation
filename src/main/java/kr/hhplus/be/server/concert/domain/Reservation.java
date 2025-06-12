package kr.hhplus.be.server.concert.domain;

import kr.hhplus.be.server.concert.domain.enums.ReservationStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Reservation {
    private Long id;
    private Long userId;
    private Long seatId;
    private ReservationStatus status;
    private LocalDateTime reservedAt;

    public Reservation(Long userId, Long seatId) {
        this.userId = userId;
        this.seatId = seatId;
        this.status = ReservationStatus.HOLD;
        this.reservedAt = LocalDateTime.now();
    }

    public void confirm() {
        if (status == ReservationStatus.HOLD) {
            status = ReservationStatus.CONFIRMED;
        } else throw new IllegalStateException("예약 확정 불가 상태입니다.");
    }

    public void cancel() {
        this.status = ReservationStatus.CANCELLED;
    }

    public boolean isPaid() {
        return this.status == ReservationStatus.CONFIRMED;
    }
}
