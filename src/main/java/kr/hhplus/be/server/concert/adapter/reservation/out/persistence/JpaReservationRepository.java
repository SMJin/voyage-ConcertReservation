package kr.hhplus.be.server.concert.adapter.reservation.out.persistence;

import kr.hhplus.be.server.concert.domain.reservation.Reservation;
import kr.hhplus.be.server.concert.domain.reservation.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaReservationRepository implements ReservationRepository {

    private final SpringDataReservationJpaRepository jpa;

    @Override
    public Optional<Reservation> findById(Long id) {
        return jpa.findById(id);
    }

    @Override
    public void save(Reservation reservation) {
        jpa.save(reservation);
    }
}
