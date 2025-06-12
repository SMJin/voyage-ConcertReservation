package kr.hhplus.be.server.concert.adapter.out.persistence.reservation;

import kr.hhplus.be.server.concert.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationJpaRepository extends JpaRepository<Reservation, Long> {
}
