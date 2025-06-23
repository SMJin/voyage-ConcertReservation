package kr.hhplus.be.server.concert.adapter.out.persistence.payment;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentJapRepository extends JpaRepository<PaymentJpaEntity, Long> {
}
