package kr.hhplus.be.server.common.response.error.type;

public enum ErrorType {
    FIELD,       // 필드 검증 오류 (ex. 이메일 형식 오류)
    GLOBAL,      // 객체 레벨 오류 (ex. 비밀번호 ≠ 비밀번호 확인)
    BUSINESS     // 비즈니스 로직 오류 (ex. 이미 존재하는 사용자)
}