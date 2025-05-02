package kr.hhplus.be.server.support;

import java.lang.reflect.Method;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

@Aspect
public class CustomAspect {

  private final SpelExpressionParser parser = new SpelExpressionParser();

  // @DistributedLock이면 적용
  @Around("@annotation(kr.hhplus.be.server.support.DistributedLock)")
  public void aroundDistributedLock(ProceedingJoinPoint joinPoint) {

  }

  private String generateLockKey(ProceedingJoinPoint joinPoint) {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    String[] parameterNames = signature.getParameterNames();
    Object[] args = joinPoint.getArgs();

    EvaluationContext context = new StandardEvaluationContext();

    for (int i = 0; i < parameterNames.length; i++) {
      context.setVariable(parameterNames[i], args[i]);
    }

    DistributedLock annotation = signature.getMethod().getAnnotation(DistributedLock.class);
    Expression expression = parser.parseExpression(annotation.key());
    return expression.getValue(context, String.class);
  }
}
