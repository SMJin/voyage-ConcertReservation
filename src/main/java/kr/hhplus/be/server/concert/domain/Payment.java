package kr.hhplus.be.server.concert.domain;

import java.time.LocalDateTime;

public class Payment {
    private Long id;
    private Long reservationId;
    private int amount;
    private boolean success;
    private LocalDateTime paidAt;
}
