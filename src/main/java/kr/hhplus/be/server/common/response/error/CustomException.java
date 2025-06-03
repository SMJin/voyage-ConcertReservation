package kr.hhplus.be.server.common.response.error;
import kr.hhplus.be.server.common.util.MessageUtil;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException {

    private final String messageCode;
    private final HttpStatus status;

    public CustomException(String messageCode, HttpStatus status) {
        this.messageCode = messageCode;
        this.status = status;
    }
}

