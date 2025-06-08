package kr.hhplus.be.server.concert.adapter.payment.out.persistence;

import kr.hhplus.be.server.concert.domain.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataPaymentJpaRepository extends JpaRepository<Payment, Long> {
}
