package kr.hhplus.be.server.concert.adapter.out.persistence.payment;

import kr.hhplus.be.server.concert.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentJapRepository extends JpaRepository<Payment, Long> {
}
