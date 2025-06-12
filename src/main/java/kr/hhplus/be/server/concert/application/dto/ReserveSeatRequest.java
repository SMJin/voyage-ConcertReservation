package kr.hhplus.be.server.concert.application.dto;

import lombok.Getter;

@Getter
public class ReserveSeatRequest {
    private String date;
    private Long seatId;
}

