package kr.hhplus.be.server.common.aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {
    String key(); // SpEL 표현식
    long waitTime() default 5; // 락 시도 대기 시간 (초)
    long leaseTime() default 10; // 락 유지 시간 (초)
}
