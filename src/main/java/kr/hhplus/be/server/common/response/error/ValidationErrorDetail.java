package kr.hhplus.be.server.common.response.error;

import kr.hhplus.be.server.common.response.error.type.ErrorType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ValidationErrorDetail {
    private ErrorType type;     // FIELD / GLOBAL / BUSINESS
    private String target;      // 대상 필드명, 객체명, 도메인 키 등
    private String reason;      // 오류 메시지
}

