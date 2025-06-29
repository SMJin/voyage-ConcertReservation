# 💰 Cash (X) Cache (O)
## 💰Memory Cache vs External Cache
| 항목    | Memory Cache                      | External Cache                      |
|-------|-----------------------------------|-------------------------------------|
| 저장 위치 | **애플리케이션 내부 메모리** (`JVM`, `heap`) | **외부 캐시 서버** (`Redis`, `Memcached`) |
| 속도    | 매우 빠름 (same process)              | 빠르지만 네트워크 오버헤드 존재                   |
| 확장성   | 한 인스턴스에 종속적 → **분산 어려움**          | 여러 인스턴스에서 **공유 가능**                 |
| 장애 시  | 애플리케이션 종료 시 데이터 사라짐               | **별도 서버 관리**, Persistence 설정 가능     |
| 사용 예시 | Guava, Caffeine 등                 | Redis, Memcached                    |

## 💰 Cache Hit vs Cache Miss
### 💸 `Cache Hit`:
- 요청한 데이터가 `캐시에 존재` → 빠르게 반환
> 예) GET /user/1 → 캐시된 사용자 정보 반환
### 💸 `Cache Miss`:
- `캐시에 없음` → 원본 DB 또는 서비스에서 가져와 캐시에 저장
> 예) GET /user/1 → 캐시에 없음 → DB 조회 → 캐시에 저장 후 반환

## 💰 Expiration (만료 정책)
> 일정 시간이 지나면 `자동 삭제`되는 설정
- 대표적인 옵션:
  - 💸 `TTL (Time To Live)`: 
    - 캐시 생성 후 일정 시간이 지나면 만료 (예: 10분)
  - 💸 `TTI (Time To Idle)`: 
    - 마지막 접근 이후 일정 시간 사용 안 하면 만료
> Redis: `SET key value EX 60` → 60초 후 자동 만료

## 💰 Eviction (제거 정책)
> 캐시가 꽉 찼을 때, 오래된 항목을 제거하는 방식
- 대표적인 Eviction 정책:
  - 💸 `LRU (Least Recently Used)`: 
    - 가장 오래 사용되지 않은 항목 제거
  - 💸 `LFU (Least Frequently Used)`: 
    - 가장 적게 사용된 항목 제거
  - 💸 `FIFO (First-In First-Out)`: 
    - 가장 먼저 들어온 항목 제거
> - Redis: `maxmemory-policy allkeys-lru`
> - Caffeine: `maximumSize`, `expireAfterWrite`

## 💰 Cache 일관성 (Consistency)
>캐시는 원본 데이터와 달라질 수 있기 때문에 일관성 관리가 중요합니다.
### 💸 종류
- `Strong Consistency`: 
  - 원본 변경 시 캐시도 즉시 반영 (어렵고 비용 큼)
- `Eventual Consistency`: 
  - 일정 시간 후 캐시와 원본이 같아짐
- `Write-Through` / `Write-Around` / `Write-Back`: 
  - 캐시 쓰기 전략에 따라 달라짐
### 💸 흔한 전략
- `Cache Aside (Lazy-loading, 수동 캐싱)`:
  - 읽을 때 캐시 → 없으면 DB → 캐시에 저장
  - 변경 시 DB 업데이트 후 `캐시 무효화(invalidate)`
```java
// READ
@Cacheable(value = "user", key = "#id")
public User getUser(Long id) {
    return userRepository.findById(id).orElseThrow();
}

// WRITE
@CacheEvict(value = "user", key = "#id")
public void updateUser(Long id, UserDto dto) {
    userRepository.save(dto.toEntity());
}
```
- `Write-Through`:
  - 캐시에 쓰면 DB에도 동시에 저장
  - 캐시가 `항상 최신 상태` 유지
```java
// Spring Cache는 기본적으로 Write-Through를 직접 지원하지 않기에,
// 다음 메서드처럼 직접 구현해야 한다.
public void updateUser(Long id, UserDto dto) {
    User user = dto.toEntity();
    redisTemplate.opsForValue().set("user:" + id, user);
    userRepository.save(user);
}
```
- `Write-Back`:
  - 캐시에 먼저 쓰고, 일정 시간 후 DB에 반영
  - 성능은 좋지만 데이터 손실 위험 있음
  - 주로 `Redis` 같은 외부 캐시에서 사용
```java
// Redis에서 Write-Back을 구현하려면 별도의 스케줄러가 필요
@Scheduled(fixedDelay = 60000) // 1분마다 실행
public void flushCacheToDB() {
    Map<String, User> users = redisTemplate.opsForHash().entries("users");
    for (Map.Entry<String, User> entry : users.entrySet()) {
        userRepository.save(entry.getValue());
    }
}
```
- `Write-Around`:
  - 캐시에 쓰지 않고 DB에만 저장
  - 다음 읽기 요청 시 캐시에서 조회
  - 캐시가 자주 사용되지 않는 경우 유용
```java
// 캐시를 사용하지 않고 DB에만 저장
public void updateUser(Long id, UserDto dto) {
    userRepository.save(dto.toEntity());
}
// 다음 읽기 요청 시 캐시에서 조회
@Cacheable(value = "user", key = "#id")
public User getUser(Long id) {
    return userRepository.findById(id).orElseThrow();
}
```
