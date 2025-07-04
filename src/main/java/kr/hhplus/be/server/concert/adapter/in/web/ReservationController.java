package kr.hhplus.be.server.concert.adapter.in.web;

import kr.hhplus.be.server.concert.application.dto.ReserveSeatRequest;
import kr.hhplus.be.server.concert.application.port.in.PayForSeatUseCase;
import kr.hhplus.be.server.concert.application.port.in.ReserveSeatUseCase;
import kr.hhplus.be.server.queue.application.service.QueueService;
import kr.hhplus.be.server.user.entity.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/api/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final ReserveSeatUseCase reserveSeatUseCase;
    private final PayForSeatUseCase payForSeatUseCase;
    private final QueueService queueService;
    private final RedisTemplate<String, Object> redisTemplate;

    @PostMapping
    public ResponseEntity<?> reserveSeat(@AuthenticationPrincipal CustomUserDetails userDetails,
                                       @RequestHeader("Queue-Token") String queueToken,
                                       @RequestBody ReserveSeatRequest request) {

        log.info("User {} is trying to reserve a seat with request: {}", userDetails.getUsername(), request);
        log.info("Queue Token: {}", queueToken);

        // 대기열 토큰 검증
        if (!queueService.canProceed(queueToken)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("대기열 순서가 아닙니다. 잠시 후 다시 시도해주세요.");
        }

        reserveSeatUseCase.reserve(userDetails.getUserId(), request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{reservationId}/pay")
    public ResponseEntity<?> payForSeat(@AuthenticationPrincipal CustomUserDetails userDetails,
                                      @PathVariable Long reservationId,
                                      @RequestBody int amount) {
        payForSeatUseCase.pay(userDetails.getUserId(), reservationId, amount);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/ranking/soldout")
    public ResponseEntity<?> getSoldoutRanking() {
        // Redis에서 매진 순서대로 콘서트 ID 반환
        Set<Object> concertIds = redisTemplate.opsForZSet().range("concert:soldout:ranking", 0, -1);
        List<Long> ranking = concertIds.stream().map(id -> (Long) id).toList();
        return ResponseEntity.ok(ranking);
    }
}
