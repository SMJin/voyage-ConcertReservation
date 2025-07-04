package kr.hhplus.be.server.common.aop;

import kr.hhplus.be.server.common.aop.annotation.DistributedLock;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Aspect
@Component
@RequiredArgsConstructor
public class DistributedLockAspect {

    private final RedissonClient redissonClient;

    @Around("@annotation(distributedLock)") // AspectJ 어노테이션을 사용하여 메소드 실행 전후에 적용
    public Object applyLock(ProceedingJoinPoint joinPoint, // ProceedingJoinPoint는 메소드 실행을 제어할 수 있는 객체
                            DistributedLock distributedLock) throws Throwable { // DistributedLock 어노테이션을 통해 메소드에 정의된 락 정보를 가져옴
        String lockKey = parseKey(distributedLock.key(), joinPoint);    // SpEL 표현식을 사용하여 락 키를 생성 // Spring Expression Language
        RLock lock = redissonClient.getLock(lockKey);

        boolean acquired = false;   // 락 획득 여부를 나타내는 변수
        try {
            acquired = lock.tryLock(distributedLock.waitTime(), distributedLock.leaseTime(), TimeUnit.SECONDS);
            if (!acquired) {
                throw new IllegalStateException("Could not acquire distributed lock for key: " + lockKey);
            }
            return joinPoint.proceed();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Lock acquisition interrupted", e);
        } finally {
            if (acquired && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * SpEL 표현식을 사용하여 락 키를 생성합니다.
     * @param keyExpression SpEL 표현식
     * @param joinPoint ProceedingJoinPoint 객체로 메소드 실행 정보를 가져옵니다.
     * @return 락 키 문자열
     */
    private String parseKey(String keyExpression, ProceedingJoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        EvaluationContext context = new StandardEvaluationContext();
        String[] paramNames = methodSignature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        for (int i = 0; i < args.length; i++) {
            context.setVariable(paramNames[i], args[i]);
        }

        ExpressionParser parser = new SpelExpressionParser();
        return parser.parseExpression(keyExpression).getValue(context, String.class);
    }
}
