package kr.hhplus.be.server.queue.adapter.in.web;

import kr.hhplus.be.server.queue.application.service.QueueService;
import kr.hhplus.be.server.queue.domain.QueueStatus;
import kr.hhplus.be.server.queue.domain.QueueToken;
import kr.hhplus.be.server.user.entity.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/queue")
@RequiredArgsConstructor
public class QueueController {
    private final QueueService queueService;

    @PostMapping("/token")
    public ResponseEntity<QueueToken> issueToken(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        QueueToken token = queueService.issueToken(userId);
        return ResponseEntity.ok(token);
    }

    @GetMapping("/status")
    public ResponseEntity<QueueStatus> getStatus(@RequestParam String token) {
        Optional<QueueStatus> status = queueService.getStatus(token);
        return status.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/token")
    public ResponseEntity<Void> removeToken(@RequestParam String token) {
        queueService.removeToken(token);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/can-proceed")
    public ResponseEntity<Boolean> canProceed(@RequestParam String token) {
        boolean canProceed = queueService.canProceed(token);
        return ResponseEntity.ok(canProceed);
    }
} 