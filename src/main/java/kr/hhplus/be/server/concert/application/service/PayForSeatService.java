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

    /**
     * 좌석 결제 서비스
     * 
     * 동시성 제어 전략:
     * 1. 분산락(@DistributedLock): 여러 서버 간 동시성 제어
     * 2. Redis 좌석 락킹: 빠른 상태 체크 + 예약자 검증
     * 3. DB 비관적 락: 실제 데이터 정합성 보장 (User, Reservation, Seat 엔티티)
     * 4. DB 낙관적 락: 예약/좌석 데이터 저장 시 동시성 제어 (@Version)
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @DistributedLock(key = "'lock:concert:payForSeat-user:#userId-reservation:#reservationId'", waitTime = 5, leaseTime = 3)
    public void pay(Long userId, Long reservationId, int amount) {
        
        // 1단계: 예약정보 조회 (비관적 락 적용)
        // - PESSIMISTIC_WRITE 락으로 예약 정보를 읽는 순간부터 락 획득
        // - 다른 트랜잭션이 같은 예약을 동시에 결제하지 못하게 막음
        // - 결제 중복 방지를 위한 핵심 동시성 제어
        Reservation reservation = reservationPort.findWithLockById(reservationId)
                .orElseThrow(() -> new CustomException(HttpStatus.CONFLICT, "예약 정보를 찾을 수 없습니다."));

        // 2단계: 예약이 이미 결제되었는지 검증
        // - 비관적 락이 걸린 상태에서 안전하게 상태 확인
        if (reservation.isPaid()) {
            throw new CustomException(HttpStatus.CONFLICT, "이미 결제된 예약입니다.");
        }

        // 3단계: 좌석 정보 확인 (비관적 락 적용)
        // - PESSIMISTIC_WRITE 락으로 좌석 정보를 읽는 순간부터 락 획득
        // - 좌석 상태 변경 시 동시성 충돌 방지
        Seat seat = seatPort.findWithLockById(reservation.getSeatId())
                .orElseThrow(() -> new CustomException(HttpStatus.CONFLICT, "예약 가능한 좌석이 아닙니다."));
        Long seatId = seat.getId();

        // 4단계: Redis에서 빠른 좌석 상태 체크 (성능 최적화)
        // - Redis의 원자적 연산으로 좌석 점유 상태 확인
        // - DB 접근 전 빠른 검증으로 성능 향상
        if (reservationLockPort.isSeatLocked(seatId)) {
            throw new CustomException(HttpStatus.CONFLICT, "이미 점유된 좌석입니다.");
        }

        // 5단계: 예약 대기열에 등록되어 있는지 검증
        // - Redis에서 예약자 정보 확인
        // - 본인이 예약한 좌석인지 검증
        Long reservedUserId = reservationLockPort.getReservedUserId(seatId);
        if (!reservedUserId.equals(userId)) {
            throw new CustomException(HttpStatus.CONFLICT, "다른 사용자에게 예약되어 있는 좌석입니다.");
        }

        // 6단계: 유저 포인트(잔액) 차감 (비관적 락 적용)
        // - PESSIMISTIC_WRITE 락으로 유저 정보를 읽는 순간부터 락 획득
        // - 포인트 차감 시 동시성 충돌 방지 (중복 차감, 잔액 부족 등)
        // - 결제의 핵심인 포인트 처리에 가장 중요한 동시성 제어
        User user = userRepository.findWithLockById(userId)
                .orElseThrow(() -> new CustomException(HttpStatus.CONFLICT, "유저 정보를 찾을 수 없습니다."));
        
        // 잔액 검증 (비관적 락이 걸린 상태에서 안전하게 확인)
        if (user.getBalance() == null || user.getBalance() < amount) {
            throw new CustomException(HttpStatus.CONFLICT, "포인트(잔액)가 부족합니다. 결제에 실패했습니다.");
        }
        long newBalance = user.getBalance() - amount;
        if (newBalance < 0) {
            throw new CustomException(HttpStatus.CONFLICT, "잔액이 부족하여 결제할 수 없습니다.");
        }
        
        // 포인트 차감 (비관적 락이 걸린 상태에서 안전하게 수정)
        user.addBalance(Long.valueOf(-amount));
        userRepository.save(user); // User 엔티티의 @Version으로 낙관적 락 자동 적용

        // 7단계: 결제 성공 처리 (비관적 락이 걸린 상태에서 안전하게 처리)
        // - 예약 확정 (비관적 락이 걸린 reservation 객체 수정)
        reservation.confirm();
        
        // - 좌석 할당 (비관적 락이 걸린 seat 객체 수정)
        seatPort.assignToUser(seatId, reservation.getUserId());
        
        // - 예약 정보 저장 (낙관적 락 자동 적용)
        //   Reservation 엔티티의 @Version 필드로 동시성 제어
        //   충돌 시 OptimisticLockException 발생
        reservationPort.save(reservation);
    }
}
