package kr.hhplus.be.server.concert.application.port.out;

import kr.hhplus.be.server.concert.domain.Seat;

import java.util.Optional;

public interface SeatPort {
    Optional<Seat> findById(Long id);
    void save(Seat seat);
    void assignToUser(Long seatId, Long userId);
    void release(Long seatId);
}