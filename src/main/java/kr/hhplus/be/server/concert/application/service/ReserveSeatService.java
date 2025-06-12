package kr.hhplus.be.server.concert.application.service;

import kr.hhplus.be.server.common.response.error.CustomException;
import kr.hhplus.be.server.concert.application.dto.ReserveSeatRequest;
import kr.hhplus.be.server.concert.application.port.in.ReserveSeatUseCase;
import kr.hhplus.be.server.concert.application.port.out.ReservationLockPort;
import kr.hhplus.be.server.concert.application.port.out.ReservationPort;
import kr.hhplus.be.server.concert.domain.Reservation;
import kr.hhplus.be.server.concert.domain.Seat;
import kr.hhplus.be.server.concert.application.port.out.SeatPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class ReserveSeatService implements ReserveSeatUseCase {

    private final SeatPort seatPort;
    private final ReservationPort reservationPort;
    private final ReservationLockPort reservationLockPort;

    @Override
    public void reserve(Long userId, ReserveSeatRequest request) {
        // 1. 예약 가능한 상태인지 검증
        if (reservationLockPort.isSeatLocked(request.getSeatId())) {
            throw new CustomException(HttpStatus.CONFLICT, "이미 점유된 좌석입니다.");
        }

        // 2. 좌석 정보 확인
        Seat seat = seatPort.findById(request.getSeatId())
                .orElseThrow(() -> new CustomException(HttpStatus.CONFLICT, "좌석을 찾을 수 없습니다."));

        // 3. 좌석 예약
        seat.hold(); // 상태 변경

        // 4. 예약 정보 저장
        Reservation reservation = new Reservation(userId, request.getSeatId());
        reservationPort.save(reservation);

        // 5. 5분간 결제 가능
        reservationLockPort.lockSeat(request.getSeatId(), userId, Duration.ofMinutes(5));
    }
}
