package kr.hhplus.be.server.concert.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class Payment {
    private Long id;
    private Long reservationId;
    private int amount;
    private boolean success;
    private LocalDateTime paidAt;
}
