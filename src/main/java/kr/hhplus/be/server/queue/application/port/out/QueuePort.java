package kr.hhplus.be.server.queue.application.port.out;

import kr.hhplus.be.server.queue.domain.QueueStatus;
import kr.hhplus.be.server.queue.domain.QueueToken;

import java.util.Optional;

public interface QueuePort {
    QueueToken issueToken(Long userId);
    Optional<QueueStatus> getStatus(String token);
    void removeToken(String token);
    boolean validateToken(String token);
} 