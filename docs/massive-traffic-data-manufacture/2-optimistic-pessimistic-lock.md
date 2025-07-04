## 🦢 낙관적/비관적 락 적용 판단 기준
### 🦆 낙관적 락
- 주로 동시성 충돌이 드물지만, `성능`이 중요한 경우
- 충돌이 발생 시, `롤백`으로 처리한다.
- ex. 콘서트 정보 수정, 포인트 충전 등
### 🦆 비관적 락
- 동시성 충돌이 자주 발생하며, `데이터 정합성`이 중요한 경우
- 충돌이 발생하지 않도록 `락`을 걸어, 반드시 `순차적`으로 처리한다.
- ex. 좌석 예약, 결제 등

## 🦢 수정 실패 허용 가능 여부 판단 기준
### 🦆 수정 실패(충돌) 허용 가능
- 충돌이 발생해도 사용자에게 “다시 시도해 주세요” 안내가 가능
- 데이터가 잠깐 일관성이 깨져도 큰 문제가 없는 경우
- ex. 게시글 동시 수정, 포인트 적립 등
### 🦆 수정 실패(충돌) 허용 불가
- 반드시 한 번에 하나의 트랜잭션만 성공해야 함
- 데이터가 잘못되면 심각한 비즈니스 오류(더블부킹, 재고 마이너스 등) 발생
- ex. 좌석 예약, 결제, 재고 차감 등

## 🦢 Lock 충돌/경합 발생 시나리오 예시
### 🦆 낙관적 락 충돌 시나리오
1. 두 사용자가 동시에 같은 좌석을 예약하려고 함
2. 둘 다 DB에서 `같은 버전의 row`를 읽음
3. 한 명이 먼저 예약 확정 → `version 증가`, `커밋`
4. 나중에 커밋하는 트랜잭션은 `version이 달라져서 OptimisticLockException` 발생
→ “`예약 실패, 다시 시도해 주세요`” 안내
### 🦆 비관적 락 경합 시나리오
1. 두 사용자가 동시에 같은 좌석을 예약하려고 함
2. 한 명이 먼저 row를 `비관적 락(PESSIMISTIC_WRITE)`으로 읽음
3. 다른 사용자는 `락이 풀릴 때까지 대기`(혹은 타임아웃/실패)
4. 첫 번째 트랜잭션이 `커밋/롤백하면 락 해제`, 그제서야 두 번째 트랜잭션이 진행
→ “`동시성 충돌 없이 한 명만 성공`, 데이터 정합성 보장”