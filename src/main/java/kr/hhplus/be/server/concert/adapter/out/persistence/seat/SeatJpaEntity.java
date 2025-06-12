package kr.hhplus.be.server.concert.adapter.out.persistence.seat;

import jakarta.persistence.*;
import kr.hhplus.be.server.concert.domain.enums.SeatStatus;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SeatJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long concertId;

    @Enumerated(EnumType.STRING)
    private SeatStatus status;

    private LocalDateTime heldAt;

    public void hold() {
        this.status = SeatStatus.HOLD;
    }
}
