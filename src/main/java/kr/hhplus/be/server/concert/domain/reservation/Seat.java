package kr.hhplus.be.server.concert.domain.reservation;

import jakarta.persistence.*;
import kr.hhplus.be.server.concert.domain.reservation.enums.SeatStatus;

import java.time.LocalDateTime;

@Entity
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long concertId;

    @Enumerated(EnumType.STRING)
    private SeatStatus status;

    private LocalDateTime heldAt;

}
