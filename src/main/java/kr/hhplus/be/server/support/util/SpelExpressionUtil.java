package kr.hhplus.be.server.support.util;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpelExpressionUtil {

  private final ExpressionParser spelExpressionParser;

  public <T> T parse(SpelParseRequest request, Class<T> clazz) {
    EvaluationContext context = new StandardEvaluationContext();

    for (int i = 0; i < request.parameterNames().length; i++) {
      context.setVariable(request.parameterNames()[i], request.args()[i]);
    }

    Expression result = spelExpressionParser.parseExpression(request.expression());
    return result.getValue(context, clazz);
  }

  @Builder
  public record SpelParseRequest(
      String expression,
      String[] parameterNames,
      Object[] args
  ) {

  }
}