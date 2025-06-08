package kr.hhplus.be.server.concert.adapter.payment.out.persistence;

import kr.hhplus.be.server.concert.domain.payment.Payment;
import kr.hhplus.be.server.concert.domain.payment.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaPaymentRepository implements PaymentRepository {

    private final SpringDataPaymentJpaRepository jpa;

    @Override
    public Optional<Payment> findById(Long id) {
        return jpa.findById(id);
    }

    @Override
    public void save(Payment payment) {
        jpa.save(payment);
    }
}
