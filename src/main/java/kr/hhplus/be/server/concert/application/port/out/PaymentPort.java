package kr.hhplus.be.server.concert.application.port.out;

import kr.hhplus.be.server.concert.domain.Payment;

import java.util.Optional;

public interface PaymentPort {
    Optional<Payment> findById(Long id);
    void save(Payment payment);
}
