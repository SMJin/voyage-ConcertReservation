package kr.hhplus.be.server.concert.adapter.out.persistence.reservation;

import jakarta.persistence.*;
import kr.hhplus.be.server.concert.domain.enums.ReservationStatus;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ReservationJpaEntity {

    // 낙관적 락을 위한 버전 필드
    @Version
    private Long version;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long seatId;


    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    private LocalDateTime reservedAt;

}
