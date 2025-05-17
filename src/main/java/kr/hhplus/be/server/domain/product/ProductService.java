package kr.hhplus.be.server.domain.product;

import static java.time.format.DateTimeFormatter.BASIC_ISO_DATE;

import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import kr.hhplus.be.server.domain.product.ProductDto.ProductIdWithTotalSales;
import kr.hhplus.be.server.domain.product.ProductDto.ProductInfo;
import kr.hhplus.be.server.domain.product.ProductDto.ProductWithQuantity;
import kr.hhplus.be.server.domain.product.ProductDto.ProductWithRank;
import kr.hhplus.be.server.domain.product.ProductDto.Top5SellingProducts;
import kr.hhplus.be.server.support.CacheKeyHolder;
import kr.hhplus.be.server.support.util.TimeUtil;
import kr.hhplus.be.server.support.util.TimeUtil.Between;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

  private static final int TOP5_SELLING_PRODUCT_HOUR_RANGE = 1;
  private static final int MAXIMUM_PRODUCT_RANKING_COUNT = 100;

  private final ProductRepository productRepository;

  public List<ProductInfo> findAllByProductOptionIds(List<Long> productOptionIds) {
    return productRepository.findAllByProductOptionIds(productOptionIds).stream()
        .map(ProductInfo::from)
        .toList();
  }

  @Transactional
  public void deductInventory(ProductDeductCommand productDeductCommand) {
    if (productDeductCommand == null || productDeductCommand.isEmpty()) {
      throw new ProductBusinessException("상품 재고 차감 커맨드는 null이거나 차감할 상품 항목이 비어있을 수 없습니다.");
    }

    List<Long> productOptionIds = productDeductCommand.productOptionIds();
    List<ProductInventory> productInventories = productRepository.findProductInventoriesForUpdateByProductOptionIds(
        productOptionIds);

    for (ProductInventory productInventory : productInventories) {
      Long productOptionId = productInventory.getProductOption().getId();
      Long amount = productDeductCommand.getAmount(productOptionId);

      if (amount != null) {
        productInventory.deduct(amount);
      }
    }

    productRepository.saveAll(productInventories);
  }

  public Top5SellingProducts findTop5SellingProducts() {
    LocalDate now = LocalDate.now();
    String yyyyMMdd = now.format(BASIC_ISO_DATE);
    CacheKeyHolder<String> key = ProductCacheKey.PRODUCT_SELLING_RANK.value(yyyyMMdd);
    List<Pair<Long, Long>> productIdRankPairs = productRepository.findAllTopSellingProducts(key, 0,
        5);

    List<Long> productIds = productIdRankPairs.stream()
        .map(Pair::getFirst)
        .toList();
    List<Product> products = productRepository.findAllByIdIn(productIds);

    Map<Long, ProductWithQuantity> productWithQuantities = products.stream()
        .map(ProductWithQuantity::from)
        .collect(Collectors.toMap(ProductWithQuantity::id, Function.identity()));

    List<ProductWithRank> productWithRanks = productIdRankPairs.stream()
        .map(productIdRankPair -> ProductWithRank.of(productIdRankPair, productWithQuantities))
        .filter(Objects::nonNull)
        .toList();

    return new Top5SellingProducts(now, productWithRanks);
  }

  public List<ProductWithQuantity> findAllProducts() {
    return productRepository.findAll();
  }

  public void saveAllSellingProducts() {
    Between between = TimeUtil.getBetweenHourRangeFromNow(TOP5_SELLING_PRODUCT_HOUR_RANGE);
    List<ProductIdWithTotalSales> productIds = productRepository.findAllSellingProductsWithRank(
        between.from(), between.to());

    List<ProductSellingRankView> productSellingRankViews = productIds.stream()
        .map(productIdWithRank -> ProductSellingRankView.builder()
            .productId(productIdWithRank.productId())
            .totalSales(productIdWithRank.totalSales())
            .from(between.from())
            .to(between.to())
            .build())
        .toList();
    productRepository.saveAllRankingViews(productSellingRankViews);

    String yyyyMMdd = LocalDateTime.now().plusMinutes(30).format(BASIC_ISO_DATE);
    List<ProductSellingRankView> filtered = productSellingRankViews.stream()
        .sorted(Comparator.comparing(ProductSellingRankView::getTotalSales).reversed())
        .limit(MAXIMUM_PRODUCT_RANKING_COUNT)
        .toList();
    CacheKeyHolder<String> key = ProductCacheKey.PRODUCT_SELLING_RANK.value(yyyyMMdd);
    for (ProductSellingRankView productSellingRankView : filtered) {
      productRepository.saveInRankingBoard(productSellingRankView.getProductId(),
          productSellingRankView.getTotalSales(), key);
    }
  }
}