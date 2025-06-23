package kr.hhplus.be.server.queue.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class QueueToken {
    private String token;
    private Long userId;
    private int position;
    private LocalDateTime issuedAt;
    private LocalDateTime expiresAt;
    private boolean active;

    public boolean isValid() {
        return active && LocalDateTime.now().isBefore(expiresAt);
    }

    public void deactivate() {
        this.active = false;
    }
} 