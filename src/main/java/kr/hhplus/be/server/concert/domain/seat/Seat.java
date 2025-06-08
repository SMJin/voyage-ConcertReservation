package kr.hhplus.be.server.concert.domain.seat;

import jakarta.persistence.*;
import kr.hhplus.be.server.concert.domain.seat.enums.SeatStatus;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long concertId;

    @Enumerated(EnumType.STRING)
    private SeatStatus status;

    private LocalDateTime heldAt;

    public void hold() {
        this.status = SeatStatus.HELD;
    }

}
