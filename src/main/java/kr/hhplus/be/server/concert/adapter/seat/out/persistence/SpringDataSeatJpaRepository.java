package kr.hhplus.be.server.concert.adapter.seat.out.persistence;

import kr.hhplus.be.server.concert.domain.seat.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataSeatJpaRepository extends JpaRepository<Seat, Long> {
}
