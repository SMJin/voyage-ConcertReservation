package kr.hhplus.be.server.queue.adapter.out.persistence;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.test.context.ActiveProfiles;
import redis.embedded.RedisServer;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class RedisQueueAdapterTest {

    private static RedisServer redisServer;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private RedisQueueAdapter redisQueueAdapter;

    @BeforeAll
    static void startRedis() throws IOException {
        redisServer = new RedisServer(6379);
        redisServer.start();
    }

    @AfterAll
    static void stopRedis() {
        if (redisServer != null) redisServer.stop();
    }

    @BeforeEach
    void setUp() {
        redisQueueAdapter = new RedisQueueAdapter(redisTemplate);
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushDb(); // 깨끗한 상태로 시작
    }

    @Test
    void removeExpiredTokens_shouldRemoveOnlyExpiredTokens() {
        /*
         * Given: 만료된 토큰과 유효한 토큰을 Redis에 저장하고, 만료된 토큰은 대기열에서 제거되어야 함
         * expiredKey: 만료된 토큰의 키
         * validKey: 유효한 토큰의 키
         * expiredUserId: 만료된 토큰의 사용자 ID
         * validUserId: 유효한 토큰의 사용자 ID
         */
        // given
        String expiredKey = "concert:token:expired";
        String validKey = "concert:token:valid";
        String expiredUserId = "111";
        String validUserId = "222";
        LocalDateTime now = LocalDateTime.now();

        // Redis의 HashOperations와 ZSetOperations를 사용하여 토큰과 대기열을 관리
        HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();
        ZSetOperations<String, String> zSetOps = redisTemplate.opsForZSet();

        // 만료된 토큰 정보 저장 (5분 전에 만료됨)
        hashOps.put(expiredKey, "userId", expiredUserId);
        hashOps.put(expiredKey, "expiresAt", now.minusMinutes(5).toString());

        // 유효한 토큰 정보 저장 (10분 후에 만료됨)
        hashOps.put(validKey, "userId", validUserId);
        hashOps.put(validKey, "expiresAt", now.plusMinutes(10).toString());

        // 대기열에 만료된 토큰과 유효한 토큰을 추가
        zSetOps.add("concert:queue", expiredUserId, 1.0);
        zSetOps.add("concert:queue", validUserId, 2.0);

        // when
        // 만료된 토큰 제거 메서드 호출
        redisQueueAdapter.removeExpiredTokens();

        // then
        // 만료된 토큰은 Redis에서 제거되고, 대기열에서도 제거되어야 함
        // 유효한 토큰은 Redis에 남아있고, 대기열에 남아있어야 함
        assertThat(redisTemplate.hasKey(expiredKey)).isFalse();
        assertThat(redisTemplate.hasKey(validKey)).isTrue();
        // 대기열에서 만료된 토큰의 사용자 ID는 제거되어야 하고, 유효한 토큰의 사용자 ID는 남아있어야 함
        assertThat(zSetOps.rank("concert:queue", expiredUserId)).isNull();
        assertThat(zSetOps.rank("concert:queue", validUserId)).isNotNull();
    }
}