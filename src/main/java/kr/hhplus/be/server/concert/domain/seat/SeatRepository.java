package kr.hhplus.be.server.concert.domain.seat;

import java.util.Optional;

public interface SeatRepository {
    Optional<Seat> findById(Long id);
    void save(Seat seat);
}