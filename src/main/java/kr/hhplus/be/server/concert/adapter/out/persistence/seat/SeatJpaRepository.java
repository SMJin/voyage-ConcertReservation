package kr.hhplus.be.server.concert.adapter.out.persistence.seat;

import kr.hhplus.be.server.concert.domain.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatJpaRepository extends JpaRepository<Seat, Long> {
}
