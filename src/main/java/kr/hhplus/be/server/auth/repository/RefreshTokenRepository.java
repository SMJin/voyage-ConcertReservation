package kr.hhplus.be.server.auth.repository;

import kr.hhplus.be.server.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
}
