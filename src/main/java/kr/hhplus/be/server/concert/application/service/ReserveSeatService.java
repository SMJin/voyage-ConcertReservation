package kr.hhplus.be.server.concert.application.service;

import kr.hhplus.be.server.common.aop.annotation.DistributedLock;
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
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class ReserveSeatService implements ReserveSeatUseCase {

    private final SeatPort seatPort;
    private final ReservationPort reservationPort;
    private final ReservationLockPort reservationLockPort;

    /**
     * 좌석 예약 서비스
     * 
     * 동시성 제어 전략:
     * 1. 분산락(@DistributedLock): 여러 서버 간 동시성 제어
     * 2. Redis 좌석 락킹: 빠른 상태 체크 + TTL 자동 만료
     * 3. DB 비관적 락: 실제 데이터 정합성 보장 (Seat 엔티티)
     * 4. DB 낙관적 락: 예약 데이터 저장 시 동시성 제어 (Reservation 엔티티의 @Version)
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @DistributedLock(key = "lock:concert:reserveSeat-user:#userId-seat:#request.seatId", waitTime = 5, leaseTime = 3)
    public void reserve(Long userId, ReserveSeatRequest request) {
        
        // 1단계: Redis에서 빠른 좌석 상태 체크 (성능 최적화)
        // - Redis의 setIfAbsent는 원자적 연산으로 동시성 보장
        // - TTL로 자동 만료 처리 (5분 후 자동 해제)
        if (reservationLockPort.isSeatLocked(request.getSeatId())) {
            throw new CustomException(HttpStatus.CONFLICT, "이미 점유된 좌석입니다.");
        }

        // 2단계: DB에서 비관적 락으로 좌석 정보 확인 (데이터 정합성 보장)
        // - PESSIMISTIC_WRITE 락으로 좌석 row를 읽는 순간부터 락 획득
        Seat seat = seatPort.findWithLockById(request.getSeatId())
                .orElseThrow(() -> new CustomException(HttpStatus.CONFLICT, "좌석을 찾을 수 없습니다."));

        // 3단계: 좌석 상태 변경 (비즈니스 로직)
        // - 비관적 락이 걸린 상태에서 안전하게 상태 변경
        seat.hold(); // 상태를 HOLD로 변경

        // 4단계: 예약 정보 저장 (낙관적 락 자동 적용)
        // - ReservationJpaEntity의 @Version 필드로 낙관적 락 적용
        // - JPA가 자동으로 버전 체크 및 동시성 제어
        // - 충돌 시 OptimisticLockException 발생
        Reservation reservation = new Reservation(userId, request.getSeatId());
        reservationPort.save(reservation);

        // 5단계: Redis에 좌석 락킹 (TTL 설정)
        // - 5분간 결제 가능하도록 Redis에 좌석 락킹
        // - 결제 완료 시 releaseSeat()로 해제
        // - 5분 후 자동 만료로 안전장치
        reservationLockPort.lockSeat(request.getSeatId(), userId, Duration.ofMinutes(5));

        // 6단계: 좌석 락 해제
        // 메서드 종료(트랜잭션 커밋) 후 자동으로 좌석 락 해제
    }
}
