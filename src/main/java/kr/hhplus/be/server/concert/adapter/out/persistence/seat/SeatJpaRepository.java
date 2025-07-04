package kr.hhplus.be.server.concert.adapter.out.persistence.seat;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatJpaRepository extends JpaRepository<SeatJpaEntity, Long> {
    // 콘서트 ID로 모든 좌석 조회
    List<SeatJpaEntity> findAllByConcertId(Long concertId);
}
