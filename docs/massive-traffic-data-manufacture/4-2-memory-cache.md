# 🎟 내장 캐시 VS 외장 캐시별 <br>LRU, LFU, FIFO 설정 예시
## 🎟 `내장` 캐시 - `Caffeine`
### ☕ Caffeine `기본 설정` 예시
```java
@Bean
public CacheManager cacheManager() {
    // Caffeine 캐시 매니저 설정 // 캐시 이름: "myCache"
    CaffeineCacheManager manager = new CaffeineCacheManager("myCache");
    manager.setCaffeine(    // Caffeine 캐시 설정
            // 최대 크기 1000, 10분 후 만료, 통계 기록 활성화
        Caffeine.newBuilder()
                .maximumSize(1000) // 최대 캐시 크기 설정
                .expireAfterWrite(10, TimeUnit.MINUTES) // 10분 후 만료
                .recordStats() // 통계 기록 활성화
    );
    return manager;
}
```
### ☕ Caffeine 에서 `LRU (Least Recently Used)` 설정하기
> 가장 `오래 사용되지 않은` 항목을 먼저 제거하는 정책
- 기본 설정에서 `maximumSize`를 지정하면 자동으로 적용
- 왜 maximumSize 을 지정하는 것이 LRU 를 지원하는 것이냐면 
  - `캐시가 꽉 차면` `가장 오래된 항목부터 제거`하기 때문입니다.
```java
@Bean
public CacheManager cacheManager() {
    CaffeineCacheManager manager = new CaffeineCacheManager("myCache");
    manager.setCaffeine(
        Caffeine.newBuilder()
                .maximumSize(1000) // 최대 캐시 크기 설정
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .recordStats()
    );
    return manager;
}
```
### ☕ Caffeine 에서 `LFU (Least Frequency Used)` 설정하기
> 가장 `적게 사용된` 항목을 먼저 제거하는 정책
- Caffeine은 기본적으로 LFU를 지원하지 않습니다.
- 대신 `expireAfterAccess`를 사용하여 `접근이 없는 항목을 제거`할 수 있습니다.
```java
@Bean
public CacheManager cacheManager() {
    CaffeineCacheManager manager = new CaffeineCacheManager("myCache");
    manager.setCaffeine(
        Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterAccess(10, TimeUnit.MINUTES) // 10분 동안 접근이 없으면 만료
                .recordStats()
    );
    return manager;
}
```
### ☕ Caffeine 에서 `FIFO (First In First Out)` 설정하기
> 가장 `먼저 들어온` 항목을 `먼저 제거`하는 정책
- Caffeine은 정식 FIFO 미지원

## 🎟 `외장` 캐시 - `Redis`
### 🎫 Redis `기본 설정` 예시
#### ① 기본 @Configuration 예시
```java
// Redis 서버 자체의 동작(메모리 정책 등)에는 영향을 줄 수 없다.
RedisCacheConfiguration.defaultCacheConfig()
    .entryTtl(Duration.ofMinutes(5)) // TTL 설정
```
#### ② Redis 서버 기본 설정 예시
- 주로 `redis.conf` 또는 CLI (`CONFIG SET`)로 지정
- 주요 항목:
  - `maxmemory`: Redis가 사용할 최대 메모리
  - `maxmemory-policy`: 메모리가 부족할 때 어떤 키를 제거할지 (LRU, LFU, FIFO 등)
```bash
# redis.conf 예시
maxmemory 256mb
maxmemory-policy allkeys-lru
```
### 🎫 Redis 에서 `LRU (Least Recently Used)` 설정하기
> 가장 `오래 사용되지 않은` 항목을 먼저 제거하는 정책
- Redis는 최근 사용되지 않은 키부터 제거하기 때문에,
- `maxmemory`가 설정되어 있어야 LRU가 동작한다.
#### 방법 ①: redis.conf
```bash
maxmemory 256mb
maxmemory-policy allkeys-lru
```
#### 방법 ②: CLI 명령어
```bash
127.0.0.1:6379> CONFIG SET maxmemory 256mb
127.0.0.1:6379> CONFIG SET maxmemory-policy allkeys-lru
```
### 🎫 Redis 에서 `LFU (Least Frequency Used)` 설정하기
> 가장 `적게 사용된` 항목을 먼저 제거하는 정책
> - Redis 4.0 이상부터 지원
- Redis는 내부적으로 사용 횟수에 대한 `counter`를 기록하고,
- 가장 사용 빈도가 낮은 키부터 제거한다.
#### 방법 ①: redis.conf
```bash
maxmemory 256mb
maxmemory-policy allkeys-lfu
```
#### 방법 ②: CLI 명령어
```bash
127.0.0.1:6379> CONFIG SET maxmemory-policy allkeys-lfu
```
### 🎫 Redis 에서 `FIFO (First In First Out)` 설정하기
> 가장 `먼저 들어온` 항목을 `먼저 제거`하는 정책
- Redis는 정식 FIFO (First In, First Out) 정책을 직접적으로 지원하지 않는다.
- list 자료구조를 사용하거나, 커스텀 eviction 로직을 구현해야 한다.