package kr.hhplus.be.server.concert.domain.reservation;

import jakarta.persistence.*;
import kr.hhplus.be.server.concert.domain.reservation.enums.ReservationStatus;

import java.time.LocalDateTime;

@Entity
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long seatId;


    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    private LocalDateTime reservedAt;

}
