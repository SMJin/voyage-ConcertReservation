package kr.hhplus.be.server.concert.application.service;

import kr.hhplus.be.server.common.aop.annotation.DistributedLock;
import kr.hhplus.be.server.common.response.error.CustomException;
import kr.hhplus.be.server.concert.application.port.in.PayForSeatUseCase;
import kr.hhplus.be.server.concert.application.port.out.ReservationLockPort;
import kr.hhplus.be.server.concert.application.port.out.ReservationPort;
import kr.hhplus.be.server.concert.application.port.out.SeatPort;
import kr.hhplus.be.server.concert.domain.Reservation;
import kr.hhplus.be.server.concert.domain.Seat;
import kr.hhplus.be.server.user.entity.User;
import kr.hhplus.be.server.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PayForSeatService implements PayForSeatUseCase {

    private final ReservationPort reservationPort;
    private final ReservationLockPort reservationLockPort;
    private final SeatPort seatPort;
    private final UserRepository userRepository;

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @DistributedLock(key = "lock:concert:payForSeat-user:#userId-reservation:#reservationId", waitTime = 5, leaseTime = 3)
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

        // 4. 좌석이 이미 점유되어 있는지 검증
        if (reservationLockPort.isSeatLocked(seatId)) {
            throw new CustomException(HttpStatus.CONFLICT, "이미 점유된 좌석입니다.");
        }

        // 5. 예약 대기열에 등록되어 있는지 검증
        Long reservedUserId = reservationLockPort.getReservedUserId(seatId);
        if (!reservedUserId.equals(userId)) {
            throw new CustomException(HttpStatus.CONFLICT, "다른 사용자에게 예약되어 있는 좌석입니다.");
        }

        // 6. 유저 포인트(잔액) 차감
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(HttpStatus.CONFLICT, "유저 정보를 찾을 수 없습니다."));
        if (user.getBalance() == null || user.getBalance() < amount) {
            throw new CustomException(HttpStatus.CONFLICT, "포인트(잔액)가 부족합니다. 결제에 실패했습니다.");
        }
        long newBalance = user.getBalance() - amount;
        if (newBalance < 0) {
            throw new CustomException(HttpStatus.CONFLICT, "잔액이 부족하여 결제할 수 없습니다.");
        }
        user.addBalance(Long.valueOf(-amount)); // 포인트 차감
        userRepository.save(user);

        // 7. 결제 성공 처리
        reservation.confirm(); // 예약 확정
        seatPort.assignToUser(seatId, reservation.getUserId()); // 해당 유저에 좌석 할당
        reservationPort.save(reservation); // 예약 정보 저장
    }
}
