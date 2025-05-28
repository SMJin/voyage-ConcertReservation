## 🥥 콘서트 예약 대기열 프로젝트

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
| 문서                                                      | 설명                        |
|---------------------------------------------------------|---------------------------|
| [시나리오 및 요구사항 요약 분석](docs/requirement-analysis.md)       | 프로젝트 배경 및 기능 시나리오         |
| [ERD](docs/erd.md)                                      | 데이터베이스 테이블 구조             |
| [시퀀스 다이어그램](docs/sequence-diagram.md)                   | 주요 기능 흐름도                 |
| [인프라 구성도](docs/infra-configuration.md)                  | Redis, DB 등을 포함한 시스템 아키텍처 |
| [Swagger API 문서](http://localhost:8080/swagger-ui.html) | (실행 후 접근 가능, 아직 미구현)      |
