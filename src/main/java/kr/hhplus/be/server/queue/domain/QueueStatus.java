package kr.hhplus.be.server.queue.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QueueStatus {
    private int position;
    private int totalUsers;
    private long estimatedWaitTime; // 대기 예상 시간 (초)
    private boolean isActive;
} 