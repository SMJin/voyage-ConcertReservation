package kr.hhplus.be.server.concert.application.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ConcertSoldOutEvent {
    private final Long concertId;
    private final Long soldoutTime;
}
