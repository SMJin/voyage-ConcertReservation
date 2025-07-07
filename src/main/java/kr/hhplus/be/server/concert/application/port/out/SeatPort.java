package kr.hhplus.be.server.concert.application.port.out;

import kr.hhplus.be.server.concert.domain.Seat;

import java.util.List;
import java.util.Optional;

public interface SeatPort {
    Optional<Seat> findById(Long id);
    Optional<Seat> findWithLockById(Long id);
    void save(Seat seat);
    void assignToUser(Long seatId, Long userId);
    void release(Long seatId);

    // 콘서트 ID로 모든 좌석 조회
    List<Seat> findAllByConcertId(Long concertId);

    // 콘서트의 모든 좌석이 CONFIRMED 상태인지 확인
    boolean isConcertSoldOut(Long concertId);
}