package kr.hhplus.be.server.domain.product;

import java.util.List;
import java.util.Map;

public record ProductDeductCommand(
    Map<Long, Long> productOptionIdToAmountMap
) {

  public static ProductDeductCommand from(Map<Long, Long> productOptionIdToAmountMap) {
    return new ProductDeductCommand(productOptionIdToAmountMap);
  }

  public List<Long> productOptionIds() {
    return productOptionIdToAmountMap.keySet()
        .stream()
        .toList();
  }

  public Long getAmount(Long productOptionId) {
    if (productOptionId == null) {
      return null;
    }

    return productOptionIdToAmountMap.get(productOptionId);
  }

  public boolean isEmpty() {
    return productOptionIdToAmountMap == null || productOptionIdToAmountMap.isEmpty();
  }
}
