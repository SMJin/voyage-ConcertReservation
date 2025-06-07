# 🎵 콘서트 좌석 예약/결제 서비스 설계 문서 🎵

## 1. 서비스 비즈니스 흐름
```bash
[사용자]
   ↓ 예약 요청 (좌석ID, 사용자ID, 금액)
[ReserveAndPayUseCase]
   ↓ 좌석 예약 가능 여부 확인
[Seat Entity]
   ↓ 좌석 임시 점유 (hold)
[ReservationRepository]
   ↓ 임시 예약 저장 (예약 엔티티 생성)
[PaymentService]
   ↓ 결제 시도
   ├─ 성공 → [Seat Entity] 좌석 상태 확정 (confirm)
   └─ 실패 → [Seat Entity] 좌석 상태 복구 (release)
[ReservationRepository]
   ↓ 예약 확정 저장 또는 상태 롤백
[NotificationPublisher]
   ↓ 예약 완료 알림 전송
```

## 2. 유스케이스 다이어그램 (콘서트 좌석 예약/결제)
```mermaid
%%{init: {'theme': 'base'}}%%
flowchart TD
    A[사용자 로그인 또는 접근] --> B[대기열 토큰 발급 요청]
    B --> C[대기열 등록 및 응답 대기]
    C --> D[대기열 통과 시 API 접근 허용]

    D --> E[예약 가능한 날짜 조회]
    D --> F[선택한 날짜의 좌석 조회]

    F --> G[예약 요청 - 날짜와 좌석]
    G --> H[좌석 임시 점유 처리 - 약 5분]
    H --> I[좌석 점유 상태를 Redis TTL로 저장]

    D --> J[잔액 조회]
    D --> K[잔액 충전]

    H --> L[결제 요청]
    L --> M[잔액 차감 처리]
    M --> N[좌석 확정 처리]
    N --> O[대기열 토큰 만료 처리]

    I --> P[TTL 만료 시 좌석 자동 해제 - Redis 이벤트 or 스케줄러]

    classDef core fill:#fff,stroke:#333,stroke-width:1px;
    class A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P core;
```

## 3. 시퀀스 다이어그램 (콘서트 좌석 예약/결제)
```mermaid
sequenceDiagram
    participant User
    participant Controller
    participant ReserveSeatUseCase
    participant SeatRepository
    participant Seat
    participant ReservationRepository
    participant Redis
    participant PaymentService
    participant NotificationService

    User->>Controller: POST /seats/reserve
    Controller->>ReserveSeatUseCase: reserve(command)
    ReserveSeatUseCase->>SeatRepository: findById(seatId)
    SeatRepository->>ReserveSeatUseCase: Seat
    ReserveSeatUseCase->>Seat: hold()
    Seat->>ReserveSeatUseCase: 상태 변경됨
    ReserveSeatUseCase->>Redis: setTTL(seatId, 5분)
    ReserveSeatUseCase->>ReservationRepository: save(reservation)
    ReserveSeatUseCase->>Controller: 예약 성공 응답

    Note over Redis: TTL 만료 시 자동 해제

    User->>Controller: POST /seats/pay
    Controller->>ReserveSeatUseCase: pay(command)
    ReserveSeatUseCase->>ReservationRepository: findById(reservationId)
    ReserveSeatUseCase->>PaymentService: 결제 시도

    alt 결제 성공
        PaymentService->>ReserveSeatUseCase: 결제 성공
        ReserveSeatUseCase->>Seat: confirm()
        ReserveSeatUseCase->>Redis: delete(seatId)
        ReserveSeatUseCase->>ReservationRepository: update(CONFIRMED)
        ReserveSeatUseCase->>NotificationService: sendSuccess()
        ReserveSeatUseCase->>Controller: 결제 성공 응답
    else 결제 실패
        PaymentService->>ReserveSeatUseCase: 결제 실패
        ReserveSeatUseCase->>Seat: release()
        ReserveSeatUseCase->>ReservationRepository: update(RELEASED)
        ReserveSeatUseCase->>Controller: 결제 실패 응답
    end
```

## 4. 상태 다이어그램 (콘서트 좌석 예약/결제 상태 전이)
```mermaid
%%{init: {'theme': 'base'}}%%
stateDiagram-v2
    [*] --> AVAILABLE

    AVAILABLE --> HELD : 사용자 예약 요청
    HELD --> CONFIRMED : 결제 완료
    HELD --> AVAILABLE : TTL 만료 or 결제 실패
    CONFIRMED --> CANCELLED : 예약 취소 (선택사항)
```

## 5. 테이블 정의

### SEAT Table
| 컬럼명         | 타입         | 설명                                |
|-------------|------------|-----------------------------------|
| id          | BIGINT(PK) | 좌석 ID (1\~50 등)                   |
| concert\_id | BIGINT     | 콘서트 ID (공연 정보)                    |
| status      | VARCHAR    | 상태 (AVAILABLE, HELD, CONFIRMED 등) |
| held\_at    | DATETIME   | 점유 시작 시간                          |

### RESERVATION Table
| 컬럼명          | 타입         | 설명                                   |
|--------------|------------|--------------------------------------|
| id           | BIGINT(PK) | 예약 ID                                |
| user\_id     | BIGINT     | 예약한 사용자 ID                           |
| seat\_id     | BIGINT     | 예약한 좌석 ID                            |
| status       | VARCHAR    | 예약 상태 (HELD, CONFIRMED, CANCELLED 등) |
| reserved\_at | DATETIME   | 예약 일시                                |
> #### 예약 상태 값 ENUM (ReservationStatus)
> - HELD: 임시 점유 중
> - CONFIRMED: 결제 완료 및 확정
> - RELEASED or CANCELLED: 실패 또는 만료

### PAYMENT Table
| 컬럼명         | 타입     | 제약조건 | 설명      |
|-------------|--------|------|---------|
| concert_id  | BIGINT | FK   | 콘서트 ID  |
| category_id | BIGINT | FK   | 카테고리 ID |

## 5. ERD
```mermaid
erDiagram

    USER ||--o{ POINT_HISTORY : has
    USER ||--o{ RESERVATION : makes

    RESERVATION ||--|| SEAT : reserves
    RESERVATION ||--|| PAYMENT : pays_for

    CONCERT ||--o{ SEAT : includes

    USER {
        BIGINT id PK
        VARCHAR name
        INT balance
        DATETIME created_at
    }

    POINT_HISTORY {
        BIGINT id PK
        BIGINT user_id FK
        INT amount
        VARCHAR reason
        DATETIME changed_at
    }

    CONCERT {
        BIGINT id PK
        VARCHAR title
        DATETIME start_time
    }

    SEAT {
        BIGINT id PK
        BIGINT concert_id FK
        VARCHAR status
        DATETIME held_at
    }

    RESERVATION {
        BIGINT id PK
        BIGINT user_id FK
        BIGINT seat_id FK
        VARCHAR status
        DATETIME reserved_at
    }

    PAYMENT {
        BIGINT id PK
        BIGINT reservation_id FK
        INT amount
        BOOLEAN success
        DATETIME paid_at
    }
```