package kr.hhplus.be.server.concert.adapter.reservation.out.persistence;

import kr.hhplus.be.server.concert.domain.reservation.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataReservationJpaRepository extends JpaRepository<Reservation, Long> {
}
