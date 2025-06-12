package kr.hhplus.be.server.concert.adapter.out.persistence.reservation;

import kr.hhplus.be.server.concert.application.port.out.ReservationPort;
import kr.hhplus.be.server.concert.domain.Reservation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReservationPersistenceAdapter implements ReservationPort {

    private final ReservationJpaRepository jpa;

    @Override
    public Optional<Reservation> findById(Long id) {
        return jpa.findById(id);
    }

    @Override
    public void save(Reservation reservation) {
        jpa.save(reservation);
    }
}
