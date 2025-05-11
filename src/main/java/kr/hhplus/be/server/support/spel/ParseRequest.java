package kr.hhplus.be.server.support.spel;

import lombok.Builder;

public class ParseRequest {

  private ParseRequest() {
  }

  @Builder
  public record SpelParseRequest(
      String expression,
      String[] parameterNames,
      Object[] args
  ) {
  }
}