package kr.hhplus.be.server.config.redis;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.support.CompositeCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Primary // 기본 캐시 매니저로 설정
    @Bean
    public CacheManager compositeCacheManager( // 복합 캐시 매니저 설정
            @Qualifier("caffeineCacheManager") CacheManager caffeineManager,
            @Qualifier("cacheManager") CacheManager redisManager
    ) {
        CompositeCacheManager manager = new CompositeCacheManager(caffeineManager, redisManager);
        manager.setFallbackToNoOpCache(false); // 캐시 없으면 예외
        return manager;
    }

    @Bean
    public CacheManager caffeineCacheManager() {
        Caffeine<Object, Object> caffeine = Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES) // 캐시 만료 시간 설정
                .maximumSize(1000); // 최대 캐시 크기 설정 // 1000개 항목까지 캐싱

        CaffeineCacheManager manager = new CaffeineCacheManager("localCache"); // 캐시 이름 설정
        manager.setCaffeine(caffeine); // Caffeine 설정 적용
        return manager;
    }

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10)) // 캐시 만료 시간 설정 // 10분 후 만료
                .disableCachingNullValues(); // null 값 캐싱 비활성화

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config) // 기본 캐시 설정
                .transactionAware() // 트랜잭션 지원
                .build();
    }
}
