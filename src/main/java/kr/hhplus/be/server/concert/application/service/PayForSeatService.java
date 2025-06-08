package kr.hhplus.be.server.concert.application.service;

import kr.hhplus.be.server.common.response.error.CustomException;
import kr.hhplus.be.server.concert.application.port.in.PayForSeatUseCase;
import kr.hhplus.be.server.concert.application.port.out.PaymentPort;
import kr.hhplus.be.server.concert.application.port.out.ReservationPort;
import kr.hhplus.be.server.concert.application.port.out.SeatPort;
import kr.hhplus.be.server.concert.domain.Reservation;
import kr.hhplus.be.server.concert.domain.Seat;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class PayForSeatService implements PayForSeatUseCase {

    private final SeatPort seatPort;
    private final ReservationPort reservationPort;
    private final PaymentPort paymentPort;

    @Override
    public void pay(Long reservationId, Long userId) {
        // 1. 예약 정보 조회
        Reservation reservation = reservationPort.findById(reservationId)
                .orElseThrow(() -> new CustomException(HttpStatus.CONFLICT, "예약 정보를 찾을 수 없습니다."));

        // 2. 좌석 정보 확인
        Seat seat = seatPort.findById(reservation.getSeatId())
                .orElseThrow(() -> new CustomException(HttpStatus.CONFLICT, "예약 가능한 좌석이 아닙니다."));
        if (!seat.isHeld()) {
            throw new CustomException(HttpStatus.CONFLICT, "예약 가능한 좌석이 아닙니다.");
        }

        // 3. 결제 시도
//        PaymentResult result = paymentPort.pay(userId, reservation.getAmount());
//
//        if (result.isSuccess()) {
//            // 4. 좌석 상태 확정
//            seat.confirm();
//            seatPort.save(seat);
//
//            // 5. 예약 상태 확정
//            reservation.confirm();
//            reservationPort.save(reservation);
//        } else {
//            // 6. 결제 실패 → 좌석/예약 해제
//            seat.release();
//            seatPort.save(seat);
//
//            reservation.cancel();
//            reservationPort.save(reservation);
//        }
    }
}
