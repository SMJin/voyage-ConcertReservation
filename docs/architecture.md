# 아키텍처 전략 (v0.1)

## 적용 원칙
- 복잡한 도메인 로직 / 외부 의존성이 많은 기능 → 클린 아키텍처
- 단순 조회/단일 책임 기능 → 레이어드 아키텍처

## 적용 계획
- 예약/결제 기능: 클린 아키텍처 적용
- 콘서트 조회, 포인트 충전 기능: 레이어드 아키텍처 적용

## 초기 디렉터리 구조 설계
```bash
🎵 concert/ 🎵
├── 📁 reservation (클린 아키텍처)
│   ├── 📂 domain
│   ├── 📂 application
│   ├── 📂 infrastructure
│   └── 📂 interface
├── 📁 concert (레이어드)
│   ├── 📂 controller
│   ├── 📂 service
│   └── 📂 repository
└── 📁 point (레이어드)
    ├── 📂 controller
    ├── 📂 service
    └── 📂 repository
```

### 예약/결제 시스템 Clean Architecture
###### ☆★ Reservation 도메인 로직에 대해서
```mermaid
%%{init: {'theme': 'base'}}%%
graph TD

%%== Domain Layer ==%%
subgraph Domain
    Reservation[Reservation Entity]
    ReservationStatus[ReservationStatus Enum]
    ReservationRepositoryPort[<<interface>> ReservationRepositoryPort]
end

%%== Use Case Layer ==%%
subgraph Application
    ReserveSeatUseCase[ReserveSeatUseCase]
end

%%== Inbound Adapter ==%%
subgraph Adapter-Inbound
    ReservationController[ReservationController - REST API]
end

%%== Outbound Adapter ==%%
subgraph Adapter-Outbound
    ReservationJpaAdapter[ReservationJpaAdapter]
end

%%== Infrastructure (Bean 등록 등) ==%%
subgraph Infrastructure
    SpringConfig[SpringConfig\n@Configuration]
end

%%== 관계 설정 ==%%
ReservationController --> ReserveSeatUseCase
ReserveSeatUseCase --> ReservationRepositoryPort
ReservationRepositoryPort -->|implements| ReservationJpaAdapter
SpringConfig --> ReservationJpaAdapter

ReserveSeatUseCase --> Reservation
Reservation --> ReservationStatus
```