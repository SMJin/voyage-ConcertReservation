package kr.hhplus.be.server.concert.adapter.seat.out.persistence;

import kr.hhplus.be.server.concert.domain.seat.Seat;
import kr.hhplus.be.server.concert.domain.seat.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaSeatRepository implements SeatRepository {

    private final SpringDataSeatJpaRepository jpa; // ← Spring JPA interface

    @Override
    public Optional<Seat> findById(Long id) {
        return jpa.findById(id);
    }

    @Override
    public void save(Seat seat) {
        jpa.save(seat);
    }

}

