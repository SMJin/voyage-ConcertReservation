# 인프라 구성도 (Infra Diagram)
```mermaid
graph TD

  A[Client - Web Browser] --> B[Front App]
  B --> C[Spring Boot Backend]
  C --> D[Redis - 좌석 임시 저장]
  C --> E[Database - 예약, 결제 정보]
```