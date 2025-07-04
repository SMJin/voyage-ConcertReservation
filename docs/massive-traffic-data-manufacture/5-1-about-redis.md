# Redis 자료구조별 정리 
## 1.String
> 가장 기본형: 대표적인 `Key-Value` 구조
### 📌 명령어
```bash
SET user:1:name "Juno"  # 캐시 저장(유저 이름)
GET user:1:name # 캐시 조회
INCR user:1:points  # 카운터 (포인트 증가)
DECR user:1:points  # 카운터 (포인트 감소)
SETNX lock:coupon 1 # 락 설정 (존재하지 않을 때만)
```

## 2. Set
> 중복 없는 집합
### 📌 명령어
```bash
SADD user:100:coupons "COUPON123" # 쿠폰 추가
SISMEMBER user:100:coupons "COUPON123" # 쿠폰 존재 여부 확인
SMEMBERS user:100:coupons # 쿠폰 목록 조회
```

## 3. Sorted Set
> 값마다 score(점수)를 부여하여 정렬된 집합
### 📌 명령어
```bash
ZADD game:ranking 5000 "juno" # 점수 추가
ZINCRBY game:ranking 200 "juno"       # 점수 증가
ZREVRANK game:ranking "juno"          # 높은 순위 기준으로 랭킹 조회
ZSCORE game:ranking "juno"            # 현재 점수 확인
ZREVRANGE game:ranking 0 9 WITHSCORES # Top 10
```
- 📌 **ZADD** vs **ZINCRBY**
  - ZADD: `새로운 값을 추가`하거나 `기존 값을 업데이트`
  - ZINCRBY: `기존 값에 점수를 더하는` 방식
- 📌 **ZREVRANK** vs **ZSCORE**
  - ZREVRANK: `순위를 반환` (0부터 시작)
  - ZSCORE: `현재 점수`를 반환
- 📌 **ZREVRANGE**
  - `정렬된 집합의 상위 N개`를 조회 (점수 기준 내림차순)
  - `WITHSCORES` 옵션으로 점수도 함께 반환
  - `0 9`는 `0번째부터 9번째까지`의 범위를 의미 (총 10개)

## 💬 체크포인트 – ZSet에서 score가 중복되면?
> ZSet은 (score, member) 쌍으로 저장하기 때문에
> <br>`member 값이 고유`해야 한다.

### ❗ 따라서 member 가 중복되면 기존의 member 의 score 를 덮어쓴다.
```bash
ZADD ranking 1000 "juno"
ZADD ranking 1000 "myeongjin"   # score는 같지만 member는 다르므로 OK
ZADD ranking 1100 "juno"        # "juno"는 이미 존재하므로 점수만 변경됨
```
### 📌 해결 전략 (동점자 처리):

- timestamp를 score에 더해 정렬 우선순위로 사용

```bash
score = 점수 * 100000 + (timestamp % 100000)
```
- 또는 score는 같게 두고, ZREVRANGE로 추출 후, 어플리케이션 단 정렬 처리

 