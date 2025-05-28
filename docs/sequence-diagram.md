## 유저 플로우 (요약 초안)

1. 유저는 토큰 발급 API를 통해 대기열에 진입합니다.
2. 대기열 순서에 따라 서비스 이용 권한을 획득합니다.
3. 좌석 정보를 조회하고, 원하는 좌석을 임시 예약합니다.
4. 유저는 잔액을 충전한 뒤 결제합니다.
5. 결제가 완료되면 좌석은 해당 유저에게 최종 배정됩니다.

## 🍁 전체 로직 시퀀스 다이어그램 🍁
```mermaid
sequenceDiagram
    participant Client
    participant AuthService
    participant ReservationService
    participant PaymentService
    participant Redis
    participant Database

    Client->>AuthService: 로그인 요청
    AuthService-->>Client: JWT 발급

    Client->>ReservationService: 좌석 예약 요청
    ReservationService->>Redis: 임시 좌석 저장
    Redis-->>ReservationService: 저장 완료

    Client->>PaymentService: 결제 요청
    PaymentService->>Redis: 좌석 상태 확인
    PaymentService->>Database: 결제 및 좌석 확정
    Database-->>PaymentService: 완료
    PaymentService-->>Client: 결제 성공
```

## 🍁 주요 API 시퀀스 다이어그램 🍁

#### 대기열 토큰 발급 API
```mermaid
sequenceDiagram
    actor User
    participant FE as Frontend
    participant Controller as QueueController
    participant Service as QueueService
    participant Redis

    User->>FE: 대기열 진입 요청 클릭
    FE->>Controller: POST /queue/token
    Controller->>Service: generateToken(userInfo)
    Service->>Redis: INCR queue:counter
    Service->>Redis: SET queue:user:{uuid} = 순번 (TTL: 5분)
    Service-->>Controller: 토큰 + 대기 순번 반환
    Controller-->>FE: 응답 전달
    FE-->>User: 대기 순번 표시
```

#### 좌석 임시 예약 + 결제 API
```mermaid
sequenceDiagram
    actor User
    participant Frontend
    participant Backend
    participant Redis
    participant DB

    User->>Frontend: 좌석 선택 요청
    Frontend->>Backend: 예약 API 호출 (/reserve)
    Backend->>Redis: 좌석 임시 홀딩 (TTL 설정)
    Redis-->>Backend: 홀딩 성공 여부 반환
    Backend-->>Frontend: 응답 (임시 예약 완료)
    Note over Backend,Frontend: 유저는 일정 시간 내 결제 필요

    User->>Frontend: 결제 요청
    Frontend->>Backend: 결제 API 호출 (/payment)
    Backend->>Redis: 임시 좌석 존재 여부 확인
    Redis-->>Backend: 존재 확인 (TTL 유지 중)
    Backend->>DB: 결제 처리 및 좌석 확정
    DB-->>Backend: 저장 완료
    Backend->>Redis: 해당 좌석 임시 데이터 삭제
    Backend-->>Frontend: 결제 성공 응답
```
