# 📊 JMeter 부하 테스트 보고서

## 🧰 테스트 도구 개요

- **도구**: Apache JMeter 5.6.3
- **목적**: API의 성능 및 인증 처리 부하 테스트
- **테스트 항목**: Spring Boot 기반 REST API
- **인증 방식**: JWT (Authorization 헤더 사용)

---

## ✅ 테스트 구성

### 1. Thread Group 설정

- 사용자 수 (Number of Threads): 100
- 루프 횟수 (Loop Count): 10~100
- Ramp-Up Period: 1~5초
- 총 요청 수: 약 12,000건

### 2. HTTP Request 설정

- Method: `GET`, `POST`
- 인증 헤더:
    - `Authorization: Bearer {token}`
- 기타: JSON Body 포함 시 `Header Manager`에 `Content-Type: application/json` 추가

### 3. 주요 Config/Listener 요소

- **HTTP Header Manager**
- **HTTP Cookie Manager** (세션 기반 시 필요)
- **View Results Tree** (응답 확인용)
- **Summary Report** (성능 통계)
- **JSON Extractor** (토큰 동적 처리 시)

---

## 🚨 초기 문제 및 해결 과정

| 문제                        | 원인                  | 해결 방법                              |
|---------------------------|---------------------|------------------------------------|
| 모든 요청 실패 (100% 오류)        | Authorization 헤더 누락 | Header Manager에 `Authorization` 추가 |
| 응답 시간/바이트 0               | 인증 실패 (401, 403)    | JWT 확인 및 토큰 갱신                     |
| 모든 응답이 에러인데 일부 응답 바이트는 있음 | 서버가 에러 메시지를 리턴함     | 응답 코드 확인 (`View Results Tree`)     |

---

## 📈 최종 테스트 결과 요약

### ✅ Summary Report 결과 (성공 시점 기준)

| 항목            | 값                  |
|---------------|--------------------|
| 총 요청 수        | 1,000건             |
| 평균 응답 시간      | 464ms              |
| 최대 응답 시간      | 1,137ms            |
| 에러율 (Error %) | 0.00%              |
| Throughput    | 172.8 requests/sec |
| 평균 응답 바이트     | 525.0 bytes        |

---

## 🛠 향후 개선 방향

1. **에러 응답 분류 및 비율 시각화** (`Response Assertion + Graphs`)
    - JMeter-plugins 설치
      [https://jmeter-plugins.org/install/Install/](https://jmeter-plugins.org/install/Install/)
2. **서버 상태 모니터링과 병행 테스트** (Grafana, Prometheus 연동)
3. **부하 단계를 나누어 시나리오화** (Ramp-Up 계획 수립)
4. **정상 응답만 필터링한 리포트 추출** (`jtl`, `CSV`, `HTML Report` 활용)
