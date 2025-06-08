package kr.hhplus.be.server.concert.application.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class ReserveSeatCommand {
    private final Long userId;
    private final Long seatId;
}

