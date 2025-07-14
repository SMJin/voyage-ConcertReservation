package kr.hhplus.be.server.concert.application.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Duration;

@Getter
@AllArgsConstructor
public class ReservationCreatedEvent {
    private final Long seatId;
    private final Long userId;
}
