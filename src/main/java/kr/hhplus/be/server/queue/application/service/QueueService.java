package kr.hhplus.be.server.queue.application.service;

import kr.hhplus.be.server.queue.application.port.out.QueuePort;
import kr.hhplus.be.server.queue.domain.QueueStatus;
import kr.hhplus.be.server.queue.domain.QueueToken;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QueueService {
    private final QueuePort queuePort;

    @Transactional
    public QueueToken issueToken(Long userId) {
        return queuePort.issueToken(userId);
    }

    @Transactional(readOnly = true)
    public Optional<QueueStatus> getStatus(String token) {
        if (!queuePort.validateToken(token)) {
            return Optional.empty();
        }
        return queuePort.getStatus(token);
    }

    @Transactional
    public void removeToken(String token) {
        queuePort.removeToken(token);
    }

    @Transactional(readOnly = true)
    public boolean validateToken(String token) {
        return queuePort.validateToken(token);
    }
    @Transactional(readOnly = true)
    public boolean canProceed(String token) {
        if (!queuePort.validateToken(token)) {
            return false;
        }
        return queuePort.isFirstInQueue(token);
    }

    @Scheduled(fixedRate = 60000) // 1분마다 실행
    @Transactional
    public void cleanupExpiredTokens() {
        queuePort.removeExpiredTokens();
    }
} 