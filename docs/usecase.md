# 🎵 concert 서비스 비즈니스 흐름 (초안) 🎵

## 1. 콘서트 조회
- 사용자는 콘서트 목록을 조회한다.
- 콘서트는 이름, 날짜, 좌석 수 정보를 포함한다.

## 2. 예약/결제
1. 사용자가 좌석을 선택한다.
2. 시스템은 좌석을 점유 상태로 변경한다.
3. 사용자가 결제한다.
4. 결제가 완료되면 좌석을 확정한다.
5. 결제가 실패하면 좌석 점유를 해제한다.

## 3. 포인트 충전
- 사용자가 원하는 금액만큼 포인트를 충전한다.
- 포인트는 결제에 사용된다.

# 🎵 concert 서비스 기능 단위별 유스-케이스 🎵

```mermaid
%% Mermaid Use Case Diagram
%% 참고: GitHub나 Notion 일부에서는 mermaid 지원 필요

%%{init: {"theme": "default"}}%%
%% Actor-UseCase 구조

  %% 유스케이스 다이어그램은 graph 대신 `%%` + 유스케이스 형식으로 그립니다
  %% 하지만 mermaid.js 10+ 기준에선 아래 구조처럼 `graph TD`로 그리는 걸 더 자주 씁니다

  graph TD
    actorUser([사용자])
    actorSystem([시스템])

    usecase1([콘서트 조회])
    usecase2([좌석 예약])
    usecase3([결제 진행])
    usecase4([예약 내역 확인])
    usecase5([포인트 충전])
    usecase6([대기열 입장])
    usecase7([입장 토큰 검증])

    actorUser --> usecase1
    actorUser --> usecase2
    actorUser --> usecase3
    actorUser --> usecase4
    actorUser --> usecase5
    actorUser --> usecase6
    actorUser --> usecase7

    usecase6 --> actorSystem
    usecase7 --> actorSystem

```