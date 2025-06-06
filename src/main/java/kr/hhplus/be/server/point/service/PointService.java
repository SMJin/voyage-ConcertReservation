package kr.hhplus.be.server.point.service;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.point.entity.PointHistory;
import kr.hhplus.be.server.point.repository.PointHistoryRepository;
import kr.hhplus.be.server.user.entity.User;
import kr.hhplus.be.server.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class PointService {

    private final UserRepository userRepository;
    private final PointHistoryRepository pointHistoryRepository;

    public void chargePoint(String username, Long amount) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        user.addBalance(amount); // 현재 잔액 증가

        PointHistory history = new PointHistory(user, "CHARGE", amount);
        pointHistoryRepository.save(history);
    }

    public Long getBalance(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."))
                .getBalance();
    }
}

