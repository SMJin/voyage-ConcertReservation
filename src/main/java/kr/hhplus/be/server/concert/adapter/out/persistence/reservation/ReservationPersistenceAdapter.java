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
        return jpa.findById(id)
                .map(this::toDomain);
    }

    @Override
    public void save(Reservation reservation) {
        jpa.save(toJpaEntity(reservation));
    }

    private Reservation toDomain(ReservationJpaEntity entity) {
        return new Reservation(entity.getUserId(), entity.getSeatId());
    }

    private ReservationJpaEntity toJpaEntity(Reservation domain) {
        return ReservationJpaEntity.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .seatId(domain.getSeatId())
                .status(domain.getStatus())
                .reservedAt(domain.getReservedAt())
                .build();
    }
}
