package kr.hhplus.be.server.concert.domain.payment;

import kr.hhplus.be.server.concert.domain.reservation.Seat;

import java.util.Optional;

public interface PaymentRepository {
    Optional<Payment> findById(Long id);
    void save(Payment payment);
}
