package kr.hhplus.be.server.common.response.error;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException {

    private final HttpStatus status;
    private final String messageCode;

    public CustomException(HttpStatus status, String messageCode) {
        this.status = status;
        this.messageCode = messageCode;
    }
}

