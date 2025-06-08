package kr.hhplus.be.server.concert.domain.reservation;

import java.util.Optional;

public interface ReservationRepository {
    Optional<Reservation> findById(Long id);
    void save(Reservation reservation);
}
