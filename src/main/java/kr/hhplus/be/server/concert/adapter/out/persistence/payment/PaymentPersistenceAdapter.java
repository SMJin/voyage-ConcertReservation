package kr.hhplus.be.server.concert.adapter.out.persistence.payment;

import kr.hhplus.be.server.concert.application.port.out.PaymentPort;
import kr.hhplus.be.server.concert.domain.Payment;
import kr.hhplus.be.server.concert.domain.PaymentResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PaymentPersistenceAdapter implements PaymentPort {

    private final PaymentJapRepository jpa;

    @Override
    public Optional<Payment> findById(Long id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public void save(Payment payment) {
        jpa.save(toJpaEntity(payment));
    }

    @Override
    public PaymentResult pay(Long reservationId, int amount) {
        // 실제 결제 로직은 외부 결제 시스템과 연동되어야 합니다.
        // 여기서는 간단히 성공으로 가정합니다.
        String transactionId = UUID.randomUUID().toString();
        return PaymentResult.success(transactionId);
    }

    private PaymentJpaEntity toJpaEntity(Payment payment) {
        return PaymentJpaEntity.builder()
                .id(payment.getId())
                .reservationId(payment.getReservationId())
                .amount(payment.getAmount())
                .success(payment.isSuccess())
                .paidAt(payment.getPaidAt())
                .build();
    }

    private Payment toDomain(PaymentJpaEntity entity) {
        return Payment.builder()
                .id(entity.getId())
                .reservationId(entity.getReservationId())
                .amount(entity.getAmount())
                .success(entity.isSuccess())
                .paidAt(entity.getPaidAt())
                .build();
    }
}
