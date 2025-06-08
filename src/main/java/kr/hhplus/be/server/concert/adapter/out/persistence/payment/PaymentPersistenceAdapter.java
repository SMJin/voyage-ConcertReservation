package kr.hhplus.be.server.concert.adapter.out.persistence.payment;

import kr.hhplus.be.server.concert.application.port.out.PaymentPort;
import kr.hhplus.be.server.concert.domain.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

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
}
