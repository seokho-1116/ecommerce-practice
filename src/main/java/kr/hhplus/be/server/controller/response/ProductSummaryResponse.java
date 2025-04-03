package kr.hhplus.be.server.controller.response;

import java.util.List;

public record ProductSummaryResponse(
    Long id,
    String name,
    String description,
    Long basePrice,
    List<SummaryOptionResponse> options
) {

  public record SummaryOptionResponse(
      Long id,
      String name,
      String description,
      Long additionalPrice,
      Long inventory
  ) {

  }
}
