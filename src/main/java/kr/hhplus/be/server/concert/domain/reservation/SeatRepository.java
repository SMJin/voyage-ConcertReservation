package kr.hhplus.be.server.concert.domain.reservation;

import java.util.Optional;

public interface SeatRepository {
    Optional<Seat> findById(Long id);
    void save(Seat seat);
}