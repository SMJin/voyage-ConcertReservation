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
        return jpa.findById(id);
    }

    @Override
    public void save(Payment payment) {
        jpa.save(payment);
    }

    /**
     * 실제 결제 로직 대신 성공 시나리오 가정
     * (실제로는 외부 API 연동해서 호출 - TossPayments, KakaoPay 등)
     */
    @Override
    public PaymentResult pay(Long reservationId, int amount) {
        String transactionId = UUID.randomUUID().toString();
        return PaymentResult.success(transactionId);
    }
}
