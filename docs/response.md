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
"message": "잘못된 요청입니다.",
"data": null
}
```
