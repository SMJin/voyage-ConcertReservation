package kr.hhplus.be.server.concert.domain;

import kr.hhplus.be.server.concert.domain.enums.SeatStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Seat {
    private Long id;
    private Long concertId;
    private Long userId;
    private SeatStatus status;
    private int price;
    private LocalDateTime heldAt;
    public Seat(Long concertId, int price) {
        this.concertId = concertId;
        this.userId = null;
        this.status = SeatStatus.AVAILABLE;
        this.price = price;
        this.heldAt = null;
    }
    public void hold() {
        this.status = SeatStatus.HOLD;
        this.heldAt = LocalDateTime.now();
    }
    public void confirm() {
        this.status = SeatStatus.CONFIRMED;
    }
    public void release() {
        this.status = SeatStatus.AVAILABLE;
        this.heldAt = null;
    }
    public void assignToUser(Long userId) {
        this.userId = userId;
    }

}
