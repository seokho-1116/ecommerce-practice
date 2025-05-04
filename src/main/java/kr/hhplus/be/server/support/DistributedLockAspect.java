package kr.hhplus.be.server.support;

import java.util.List;
import kr.hhplus.be.server.common.exception.InternalServerException;
import kr.hhplus.be.server.domain.support.LockCommand;
import kr.hhplus.be.server.domain.support.LockTemplate;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DistributedLockAspect {

  private final SpelExpressionParser parser = new SpelExpressionParser();
  private final LockTemplate lockTemplate;

  @Around("@annotation(kr.hhplus.be.server.support.DistributedLock)")
  public Object aroundDistributedLock(ProceedingJoinPoint joinPoint) {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    String[] parameterNames = signature.getParameterNames();
    Object[] args = joinPoint.getArgs();

    EvaluationContext context = new StandardEvaluationContext();

    for (int i = 0; i < parameterNames.length; i++) {
      context.setVariable(parameterNames[i], args[i]);
    }

    DistributedLock annotation = signature.getMethod().getAnnotation(DistributedLock.class);
    Expression expression = parser.parseExpression(annotation.expression());
    List<Object> keys = (List<Object>) expression.getValue(context, List.class);

    if (keys == null || keys.isEmpty()) {
      throw new IllegalArgumentException("락 키가 비어있습니다.");
    }

    List<String> stringKeys = keys.stream()
        .map(String::valueOf)
        .toList();

    return lockTemplate.execute(() -> {
      try {
        return joinPoint.proceed();
      } catch (RuntimeException e) {
        throw e;
      } catch(Throwable e) {
        throw new InternalServerException();
      }
    }, LockCommand.of(annotation.key(), annotation.timeout(), annotation.timeUnit(), stringKeys));
  }
}
