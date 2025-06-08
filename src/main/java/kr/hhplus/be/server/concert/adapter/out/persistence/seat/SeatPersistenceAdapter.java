package kr.hhplus.be.server.concert.adapter.out.persistence.seat;

import kr.hhplus.be.server.concert.application.port.out.SeatPort;
import kr.hhplus.be.server.concert.domain.Seat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SeatPersistenceAdapter implements SeatPort {

    private final SeatJpaRepository jpa; // ← Spring JPA interface

    @Override
    public Optional<Seat> findById(Long id) {
        return jpa.findById(id);
    }

    @Override
    public void save(Seat seat) {
        jpa.save(seat);
    }

}

