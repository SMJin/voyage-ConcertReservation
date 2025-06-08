package kr.hhplus.be.server.concert.domain.payment;

import java.util.Optional;

public interface PaymentRepository {
    Optional<Payment> findById(Long id);
    void save(Payment payment);
}
