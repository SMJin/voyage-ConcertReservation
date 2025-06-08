package kr.hhplus.be.server.concert.domain.seat.enums;

import jakarta.persistence.Embeddable;

@Embeddable
public enum SeatStatus {
    AVAILABLE,   // 예약 가능한 좌석
    HELD,        // 누군가가 임시 점유 중 (5분)
    CONFIRMED    // 결제 완료로 확정된 좌석
}
