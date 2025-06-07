package kr.hhplus.be.server.concert.domain.reservation.enums;

import jakarta.persistence.Embeddable;

@Embeddable
public enum ReservationStatus {
    HELD,         // 임시 점유 상태
    CONFIRMED,    // 결제 완료 및 확정
    RELEASED      // 점유 해제됨 (시간초과 or 실패)
}
