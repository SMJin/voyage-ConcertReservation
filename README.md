## 🥥 콘서트 예약 대기열 프로젝트 🥁

- 사용자가 콘서트 좌석을 선택하고, 임시 예약 후 일정 시간 내 결제를 완료하면 좌석이 확정되는 실시간 예약 서비스입니다.
- Redis의 TTL 기능을 활용해 좌석 임시 배정을 구현하고, Spring Boot 기반 REST API와 WebSocket(STOMP)을 통해 사용자 간 실시간 동기화를 지원합니다.

## Getting Started

### Prerequisites

#### Running Docker Containers

`local` profile 로 실행하기 위하여 인프라가 설정되어 있는 Docker 컨테이너를 실행해주셔야 합니다.

```bash
docker-compose up -d
```

## 📰 Documentation

🎯 프로젝트 문서 모음입니다. 각 항목을 통해 프로젝트 흐름과 구조를 쉽게 파악할 수 있습니다.

### 📌 프로젝트 진행 및 계획
- 🔗 [마일스톤](https://github.com/SMJin/voyage-ConcertReservation/milestones)  
  → 전체 프로젝트의 일정 및 단계별 목표를 확인할 수 있습니다.
- 🔗 [이슈 현황](https://github.com/SMJin/voyage-ConcertReservation/issues)  
  → 현재까지 진행된 작업 현황과 남은 작업을 추적할 수 있습니다.

### 📋 기획 및 요구사항
- 📝 [시나리오 및 요구사항 요약 분석](docs/requirement-analysis.md)  
  → 서비스 배경, 기능 흐름, 사용자 관점의 시나리오를 정리한 문서입니다.

### 🧱 시스템 설계
- 🏗 [아키텍처 구조](docs/architecture.md)  
  → 클린 아키텍처 및 모듈 구조 등 시스템 전반 설계 구조를 설명합니다.
- 🗃 [ERD](docs/erd.md)  
  → 콘서트-날짜-좌석 기반의 데이터 테이블 구조 및 관계를 확인할 수 있습니다.
- 🔁 [시퀀스 다이어그램](docs/sequence-diagram.md)  
  → 주요 기능(예: 좌석 예약, 결제 등)의 내부 흐름을 시각적으로 표현한 문서입니다.
- 🧩 [인프라 구성도](docs/infra-configuration.md)  
  → Redis, DB 등 인프라 환경과 구성 요소들의 연동 구조를 설명합니다.

### 📡 API 문서
- 📖 [Swagger API 문서](http://localhost:8080/swagger-ui.html)  
  → 로컬 실행 후 접근 가능 (※ 현재 미구현 상태입니다)
- 📖 [공통 Response 형식](docs/response.md)  
  → 공통 응답 형식(성공/실패) 및 에러 핸들링 구조
