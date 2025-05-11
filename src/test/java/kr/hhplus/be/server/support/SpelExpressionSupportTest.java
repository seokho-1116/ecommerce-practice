package kr.hhplus.be.server.support;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Objects;
import kr.hhplus.be.server.support.spel.ParseRequest;
import kr.hhplus.be.server.support.spel.SpelExpressionSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.expression.spel.standard.SpelExpressionParser;

@SuppressWarnings("unchecked")
class SpelExpressionSupportTest {

  private final SpelExpressionSupport spelExpressionSupport;

  SpelExpressionSupportTest() {
    this.spelExpressionSupport = new SpelExpressionSupport(new SpelExpressionParser());
  }

  @DisplayName("표현식을 평가해 결과를 반환한다")
  @Test
  void testParse() {
    // given
    String expression = "{#a, #b}";
    String[] parameterNames = {"a", "b"};
    Object[] args = {1, 2};

    ParseRequest.SpelParseRequest request = ParseRequest.SpelParseRequest.builder()
        .expression(expression)
        .parameterNames(parameterNames)
        .args(args)
        .build();

    // when
    List<Integer> result = (List<Integer>) spelExpressionSupport.parse(request, List.class);

    // then
    assertThat(result).contains(1, 2);
  }

  @DisplayName("표현식의 결과가 null인 경우 null을 반환한다")
  @Test
  void testParseNull() {
    // given
    String expression = "{#c, #d}";
    String[] parameterNames = {"a", "b"};
    Object[] args = {1, 2};

    ParseRequest.SpelParseRequest request = ParseRequest.SpelParseRequest.builder()
        .expression(expression)
        .parameterNames(parameterNames)
        .args(args)
        .build();

    // when
    List<Integer> result = (List<Integer>) spelExpressionSupport.parse(request, List.class);

    // then
    assertThat(result).isNotEmpty()
        .allMatch(Objects::isNull);
  }
}