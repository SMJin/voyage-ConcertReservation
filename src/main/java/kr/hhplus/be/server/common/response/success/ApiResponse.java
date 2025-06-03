package kr.hhplus.be.server.common.response.success;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Builder
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.of(true, "요청 성공", data);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.of(true, message, data);
    }

    public static <T> ApiResponse<T> fail(String message) {
        return ApiResponse.of(false, message, null);
    }

    public static <T> ApiResponse<T> fail(String message, T data) {
        return ApiResponse.of(false, message, data);
    }
}


