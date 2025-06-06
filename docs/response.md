## 📡 API 응답 포맷 가이드

모든 API Response 는 다음과 같은 공통 구조를 따릅니다.

### ✅ 성공 응답 (Success Response)

```json
{
  "success": true,
  "message": "요청 성공",
  "data": {
    // 실제 데이터
  }
}
```

### ❌ 실패 응답 (Fail Response)
```json
{
  "success": false,
  "message": "요청 파라미터가 잘못되었습니다.",
  "data": {
    "errorType": "FIELD",
    "errors": [
      {
        "field": "email",
        "message": "이메일 형식이 올바르지 않습니다."
      },
      {
        "field": "password",
        "message": "비밀번호는 8자 이상이어야 합니다."
      }
    ]
  }
}

```
#### 필드 설명
| 필드명           | 타입      | 설명                                   |
|---------------|---------|--------------------------------------|
| `success`     | boolean | 성공 여부 (`true` or `false`)            |
| `message`     | string  | 에러 요약 메시지                            |
| `data`        | object  | 에러 상세 정보 (`null` 가능)                 |
| └ `errorType` | string  | 에러 분류: `FIELD`, `GLOBAL`, `BUSINESS` |
| └ `errors`    | array   | 필드 단위 오류 배열                          |
| └ `field`     | string  | 오류가 발생한 필드명                          |
| └ `message`   | string  | 해당 필드에 대한 오류 메시지                     |

#### 에러 타입 구분
| errorType  | 설명                             |
|------------|--------------------------------|
| `FIELD`    | 필드 단위 입력값 오류 (e.g. 이메일 형식)     |
| `GLOBAL`   | 객체 레벨 오류 (e.g. 비밀번호 ≠ 비밀번호 확인) |
| `BUSINESS` | 비즈니스 로직 오류 (e.g. 중복 사용자)       |

### 📌 예시: 비즈니스 로직 오류 (회원 중복)
```json
{
  "success": false,
  "message": "이미 존재하는 사용자입니다.",
  "data": {
    "errorType": "BUSINESS",
    "errors": []
  }
}
```
### 📌 예시: 서버 오류 (처리되지 않은 예외)
```json
{
  "success": false,
  "message": "알 수 없는 서버 오류가 발생했습니다.",
  "data": null
}
```

### 📌 응답 코드 매핑
| HTTP 상태코드 | 설명                     |
|-----------|------------------------|
| 400       | Validation 실패 (입력 오류)  |
| 401       | 인증 실패                  |
| 403       | 권한 없음                  |
| 404       | 자원 없음                  |
| 409       | 비즈니스 로직 충돌 (e.g. 중복 등) |
| 500       | 서버 내부 오류               |
