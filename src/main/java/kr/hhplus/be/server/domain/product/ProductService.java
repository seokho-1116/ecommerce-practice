package kr.hhplus.be.server.domain.product;

import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import kr.hhplus.be.server.domain.product.ProductDto.ProductIdWithRank;
import kr.hhplus.be.server.domain.product.ProductDto.ProductInfo;
import kr.hhplus.be.server.domain.product.ProductDto.ProductWithQuantity;
import kr.hhplus.be.server.domain.product.ProductDto.ProductWithQuantity.ProductWithQuantityOption;
import kr.hhplus.be.server.domain.product.ProductDto.ProductWithRank;
import kr.hhplus.be.server.domain.product.ProductDto.Top5SellingProducts;
import kr.hhplus.be.server.support.CacheKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

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
    LocalDateTime from = now.minusDays(3).atStartOfDay();
    LocalDateTime to = LocalDateTime.now();
    List<ProductIdWithRank> productIdWithRanks = productRepository.findTop5SellingProductsFromRankViewInCache(
        from,
        to
    );

    List<Long> productIds = productIdWithRanks.stream()
        .map(ProductIdWithRank::productId)
        .toList();
    List<Product> products = productRepository.findAllByIdIn(productIds);

    Map<Long, ProductWithQuantity> productWithQuantities = products.stream()
        .map(ProductWithQuantity::from)
        .collect(Collectors.toMap(ProductWithQuantity::id, Function.identity()));

    List<ProductWithRank> productWithRanks = productIdWithRanks.stream()
        .map(productIdWithRank -> {
          Long productId = productIdWithRank.productId();
          ProductWithQuantity productWithQuantity = productWithQuantities.get(productId);

          if (productWithQuantity == null) {
            return null;
          }

          Long quantity = productWithQuantity.options().stream()
              .mapToLong(ProductWithQuantityOption::quantity)
              .sum();

          return new ProductWithRank(
              productIdWithRank.rank(),
              quantity,
              productId,
              productWithQuantity.name(),
              productWithQuantity.description(),
              productWithQuantity.basePrice()
          );
        })
        .filter(Objects::nonNull)
        .toList();

    return new Top5SellingProducts(from, to, productWithRanks);
  }

  public List<ProductWithQuantity> findAllProducts() {
    return productRepository.findAll();
  }

  public void saveTop5SellingProducts() {
    LocalTime now = LocalTime.now();
    int oneHourAgo = now.getHour() - 1;
    LocalDateTime from = LocalDate.now().atTime(oneHourAgo, 0, 0, 0);
    LocalDateTime to = LocalDate.now().atTime(now.getHour(), 0, 0, 0);

    List<ProductIdWithRank> productIdWithRanks = productRepository.findTop5SellingProducts(
        from, to);

    List<ProductSellingRankView> productSellingRankViews = productIdWithRanks.stream()
        .map(productIdWithRank -> ProductSellingRankView.builder()
            .productId(productIdWithRank.productId())
            .totalSales(productIdWithRank.totalSales())
            .rank(productIdWithRank.rank())
            .from(from)
            .to(to)
            .build())
        .toList();

    productRepository.saveAllRankingViews(productSellingRankViews);
  }

  public void saveTop5SellingProductIdsInCache() {
    LocalDate now = LocalDate.now();
    LocalDateTime from = now.minusDays(3).atStartOfDay();
    LocalDateTime to = LocalDateTime.now();
    List<ProductIdWithRank> productIdWithRanks = productRepository.findTop5SellingProductsFromRankView(from,
        to);

    if (productIdWithRanks != null && !productIdWithRanks.isEmpty()) {
      productRepository.saveTop5SellingProductInCache(
          CacheKey.TOP5_SELLING_PRODUCT,
          productIdWithRanks,
          25,
          TimeUnit.HOURS
      );
    }
  }
}
