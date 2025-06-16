package kr.hhplus.be.server.concert.adapter.out.persistence.seat;

import kr.hhplus.be.server.common.response.error.CustomException;
import kr.hhplus.be.server.concert.application.port.out.SeatPort;
import kr.hhplus.be.server.concert.domain.Seat;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SeatPersistenceAdapter implements SeatPort {

    private final SeatJpaRepository jpa;

    @Override
    public Optional<Seat> findById(Long id) {
        return jpa.findById(id)
                .map(this::toDomain);
    }

    @Override
    public void save(Seat seat) {
        jpa.save(toJpaEntity(seat));
    }

    @Override
    public void assignToUser(Long seatId, Long userId) {
        SeatJpaEntity seat = jpa.findById(seatId)
                .orElseThrow(() -> new CustomException(HttpStatus.CONFLICT, "좌석 정보가 존재하지 않습니다."));
        seat.assignToUser(userId);
        jpa.save(seat);
    }

    @Override
    public void release(Long seatId) {
        SeatJpaEntity seat = jpa.findById(seatId)
                .orElseThrow(() -> new CustomException(HttpStatus.CONFLICT, "좌석 정보가 존재하지 않습니다."));
        seat.release();
        jpa.save(seat);
    }

    private Seat toDomain(SeatJpaEntity entity) {
        return new Seat(entity.getConcertId(), 0); // TODO: Add price field to SeatJpaEntity
    }

    private SeatJpaEntity toJpaEntity(Seat domain) {
        return SeatJpaEntity.builder()
                .id(domain.getId())
                .concertId(domain.getConcertId())
                .status(domain.getStatus())
                .heldAt(domain.getHeldAt())
                .build();
    }
}

