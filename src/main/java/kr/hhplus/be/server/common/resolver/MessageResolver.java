package kr.hhplus.be.server.common.resolver;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class MessageResolver {

    private final MessageSource messageSource;

    public String get(String code) {
        return messageSource.getMessage(code, null, code, Locale.getDefault());
    }

    public String get(String code, Object... args) {
        return messageSource.getMessage(code, args, code, Locale.getDefault());
    }
}

