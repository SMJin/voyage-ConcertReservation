package kr.hhplus.be.server.common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class MessageUtil {

    private final MessageSource messageSource;

    public String get(String code) {
        return messageSource.getMessage(code, null, Locale.getDefault());
    }

    public String get(String code, Object... args) {
        return messageSource.getMessage(code, args, Locale.getDefault());
    }
}

