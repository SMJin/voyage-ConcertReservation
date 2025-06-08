package kr.hhplus.be.server.concert.domain;

import kr.hhplus.be.server.concert.domain.enums.SeatStatus;

import java.time.LocalDateTime;

public class Seat {
    private Long id;
    private Long concertId;
    private SeatStatus status;
    private LocalDateTime heldAt;
    public void hold() {
        this.status = SeatStatus.HOLD;
        this.heldAt = LocalDateTime.now();
    }
    public boolean isHeld() {
        return this.status == SeatStatus.HOLD;
    }
    public void confirm() {
        this.status = SeatStatus.CONFIRMED;
    }
    public void release() {
        this.status = SeatStatus.AVAILABLE;
        this.heldAt = null;
    }

}
