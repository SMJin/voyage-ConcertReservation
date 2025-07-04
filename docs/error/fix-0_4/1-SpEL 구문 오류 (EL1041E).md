# 💥 `SpEL(SPRING Expression Language)`과 에러 분석

## ❓ 어떤 문제가 발생했는가

```log
org.springframework.expression.spel.SpelParseException:
Expression [queue:lock:removeExpiredTokens] @5:
EL1041E: After parsing a valid expression, there is still more data in the expression: 'colon(:)'
```

## 📌 원인 요약
- SpEL은 `Spring`에서 `동적으로 값을 계산`하거나 `조건을 지정`할 수 있도록 만든 표현식 언어이다.
- 위 에러는 `:(콜론)을 포함한 문자열`이 문자열로 감싸지지 않아서 발생했다.
- SpEL은 `:`을 특수기호로 해석하지 못해, `문자열이 아닌 변수같은 것으로 해석`해, 문법 에러를 낸다.

## 잘못된 코드 예시
```java
@DistributedLock(key = "queue:lock:removeExpiredTokens") // ❌ SpEL 해석 오류 발생
```
- 문자열처럼 보여도, SpEL은 내부적으로 queue 라는 변수를 찾다가 실패한다.
- 이후 :lock:removeExpiredTokens를 해석하지 못해 에러 발생.

## 🛠 해결 방법
- 문자열은 반드시 `작은따옴표 '`로 감싸야 함.
```java
@DistributedLock(key = "'queue:lock:removeExpiredTokens'") // ✅ 정상 작동
```
- 'queue:lock:removeExpiredTokens'는 `문자열로 해석`되므로 SpEL 구문 오류 없음

## 📚 SpEL 기본 문법 요약
| 문법                 | 설명                | 예시                                                      |
|--------------------|-------------------|---------------------------------------------------------|
| `#{...}`           | SpEL 표현식의 시작과 끝   | `@Value("#{1 + 2}")` → `3`                              |
| `'문자열'`            | 문자열 리터럴           | `@Cacheable(key = "'user:' + #id")` → `"user:42"`       |
| `+`, `-`, `*`, `/` | 산술 연산자            | `@Value("#{5 * 3}")` → `15`                             |
| `#paramName`       | 메서드 파라미터 참조       | `@Cacheable(key = "#userId")` (메서드 인자 중 `userId` 참조)    |
| `T(클래스).상수/메서드`    | static 필드나 메서드 참조 | `@Value("#{T(java.lang.Math).random()}")` → `0.1234...` |

### Q. 왜 Lock, Cacheable 에서는 `#{}` 없이도 SpEL이 작동하는가?
> - Spring 은 @Cacheable, @PreAuthorize, @Scheduled, @Lock 등의 
> - `특정 어노테이션`에서 해당 파라미터가 SpEL임을 미리 알고 있어서,
> - `#{} 없이도 내부적으로 SpEL로 처리`합니다.

| 상황                                                    | SpEL 표현식 필요 형태                   |
|-------------------------------------------------------|----------------------------------|
| 일반 `@Value`, `@Scheduled` 등                           | `#{...}` 필요                      |
| `@Cacheable`, `@PreAuthorize`, `@Lock(key = "...")` 등 | 내부적으로 SpEL 해석 → **`#{}` 없이도 OK** |


## 🎯 결론
- SpEL에서 문자열을 사용할 때는 반드시 `'(작은따옴표)`로 감싸야 한다.
- 그렇지 않으면 `SpEL 파서`가 이를 변수나 표현식으로 오해하고 오류가 발생한다.
- 이번 경우처럼 `락 키, 캐시 키, 권한 조건` 등에 SpEL이 자주 사용되므로 문자열 처리에 주의해야 한다.