package kr.hhplus.be.server.concert.application.service;

import kr.hhplus.be.server.concert.application.dto.ReserveSeatCommand;
import kr.hhplus.be.server.concert.application.port.in.ReserveSeatUseCase;
import kr.hhplus.be.server.concert.domain.Seat;
import kr.hhplus.be.server.concert.application.port.out.SeatPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReserveSeatService implements ReserveSeatUseCase {

    private final SeatPort seatPort;

    @Override
    public void reserve(ReserveSeatCommand command) {
        Seat seat = seatPort.findById(command.getSeatId())
                .orElseThrow(() -> new RuntimeException("Seat not found"));

        seat.hold(); // 도메인 로직
        seatPort.save(seat); // 인터페이스만 의존
    }
}
