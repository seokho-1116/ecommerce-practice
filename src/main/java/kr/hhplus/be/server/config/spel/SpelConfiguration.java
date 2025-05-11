package kr.hhplus.be.server.config.spel;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

@Configuration
public class SpelConfiguration {

  @Bean
  public ExpressionParser spelExpressionParser() {
    return new SpelExpressionParser();
  }
}