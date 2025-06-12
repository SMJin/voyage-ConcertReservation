package kr.hhplus.be.server.concert.application.port.in;

import kr.hhplus.be.server.concert.application.dto.ReserveSeatRequest;

public interface ReserveSeatUseCase {
    void reserve(Long userId, ReserveSeatRequest request);
}

