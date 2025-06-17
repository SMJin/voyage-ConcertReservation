package kr.hhplus.be.server.concert.application.service;

import kr.hhplus.be.server.common.response.error.CustomException;
import kr.hhplus.be.server.concert.application.port.in.PayForSeatUseCase;
import kr.hhplus.be.server.concert.application.port.out.PaymentPort;
import kr.hhplus.be.server.concert.application.port.out.ReservationLockPort;
import kr.hhplus.be.server.concert.application.port.out.ReservationPort;
import kr.hhplus.be.server.concert.application.port.out.SeatPort;
import kr.hhplus.be.server.concert.domain.PaymentResult;
import kr.hhplus.be.server.concert.domain.Reservation;
import kr.hhplus.be.server.concert.domain.Seat;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class PayForSeatService implements PayForSeatUseCase {

    private final ReservationPort reservationPort;
    private final PaymentPort paymentPort;
    private final ReservationLockPort reservationLockPort;
    private final SeatPort seatPort;

    private final RedissonClient redissonClient;

    @Override
    public void pay(Long userId, Long reservationId, int amount) {
        // 1. 예약정보 조회
        Reservation reservation = reservationPort.findById(reservationId)
                .orElseThrow(() -> new CustomException(HttpStatus.CONFLICT, "예약 정보를 찾을 수 없습니다."));

        // 2. 예약이 이미 결제되었는지 검증
        if (reservation.isPaid()) {
            throw new CustomException(HttpStatus.CONFLICT, "이미 결제된 예약입니다.");
        }

        // 3. 좌석 정보 확인
        Seat seat = seatPort.findById(reservation.getSeatId())
                .orElseThrow(() -> new CustomException(HttpStatus.CONFLICT, "예약 가능한 좌석이 아닙니다."));
        Long seatId = seat.getId();

        RLock lock = redissonClient.getLock("seat:" + seatId);
        try {
            if (!lock.tryLock(3, 10, TimeUnit.SECONDS)) {
                throw new CustomException(HttpStatus.CONFLICT, "잠시 후 다시 시도해주세요.");
            }

            // 4. 좌석이 이미 점유되어 있는지 검증
            if (reservationLockPort.isSeatLocked(seatId)) {
                throw new CustomException(HttpStatus.CONFLICT, "이미 점유된 좌석입니다.");
            }

            // 5. 예약 대기열에 등록되어 있는지 검증
            Long reservedUserId = reservationLockPort.getReservedUserId(seatId);
            if (!reservedUserId.equals(userId)) {
                throw new CustomException(HttpStatus.CONFLICT, "다른 사용자에게 예약되어 있는 좌석입니다.");
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "락 획득 중 인터럽트 발생");
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

        // 6. 아직 결제가 되지 않은 상태이므로, 외부 결제 시스템에 결제 요청
        // 결제 금액과 유저 ID 반환
        PaymentResult result = paymentPort.pay(reservationId, amount);

        // 7.1. 결제 성공
        if (result.isSuccess()) {
            reservation.confirm(); // 예약 확정
            seatPort.assignToUser(seatId, reservation.getUserId()); // 해당 유저에 좌석 할당
            reservationPort.save(reservation); // 예약 정보 저장
        }
        // 7.2. 결제 실패
        else {
            reservation.cancel(); // 예약 해제
            reservationPort.save(reservation); // 예약 취소로 저장
            seatPort.release(seatId); // 좌석 점유 해제
            throw new CustomException(HttpStatus.CONFLICT, "결제를 실패하였습니다."); // 결제 실패 exception
        }
    }
}
