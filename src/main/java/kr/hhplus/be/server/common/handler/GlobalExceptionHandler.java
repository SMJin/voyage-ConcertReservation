package kr.hhplus.be.server.common.handler;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.common.response.error.CustomException;
import kr.hhplus.be.server.common.response.success.ApiResponse;
import kr.hhplus.be.server.common.response.error.ValidationErrorResponse;
import kr.hhplus.be.server.common.response.error.type.ErrorType;
import kr.hhplus.be.server.common.response.error.ValidationErrorDetail;
import kr.hhplus.be.server.common.resolver.MessageResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageResolver messageResolver;

    /**
     * @Valid 유효성 검증 실패 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<ValidationErrorResponse>> handleValidationException(MethodArgumentNotValidException ex) {
        List<ValidationErrorDetail> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> ValidationErrorDetail.builder()
                        .type(ErrorType.FIELD)
                        .target(error.getField())
                        .reason(error.getDefaultMessage())
                        .build())
                .collect(Collectors.toList());

        ValidationErrorResponse response = ValidationErrorResponse.builder()
                .errors(errors)
                .build();

        String message = messageResolver.get("response.error.validation");
        return ResponseEntity.badRequest().body(ApiResponse.fail(message, response));
    }

    /**
     * IllegalArgumentException 처리
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("IllegalArgumentException 발생: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(ApiResponse.fail(ex.getMessage()));
    }

    /**
     * EntityNotFoundException Id 조회했는데 객체가 없음
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleEntityNotFoundException(EntityNotFoundException ex) {
        log.warn("EntityNotFoundException 발생: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.fail(ex.getMessage()));
    }

    /**
     * CustomException 처리 (선택적 구현)
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<String>> handleCustomException(CustomException ex) {
        String message = messageResolver.get(ex.getMessageCode());
        log.warn("CustomException 발생: {}", message);
        return ResponseEntity.status(ex.getStatus())
                .body(ApiResponse.fail(message));
    }

    /**
     * 그 외 모든 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleUnknownException(Exception ex) {
        log.error("알 수 없는 서버 오류 발생", ex);
        return ResponseEntity.internalServerError()
                .body(ApiResponse.fail("서버 내부 오류가 발생했습니다."));
    }
}

