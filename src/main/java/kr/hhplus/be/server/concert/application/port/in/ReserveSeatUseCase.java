package kr.hhplus.be.server.concert.application.port.in;

import kr.hhplus.be.server.concert.application.dto.ReserveSeatCommand;

public interface ReserveSeatUseCase {
    void reserve(ReserveSeatCommand command);
}

