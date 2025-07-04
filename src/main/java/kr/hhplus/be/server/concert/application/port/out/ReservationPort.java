package kr.hhplus.be.server.concert.application.port.out;

import kr.hhplus.be.server.concert.domain.Reservation;

import java.util.Optional;

public interface ReservationPort {
    Optional<Reservation> findById(Long id);
    Optional<Reservation> findWithLockById(Long id);
    void save(Reservation reservation);
}
